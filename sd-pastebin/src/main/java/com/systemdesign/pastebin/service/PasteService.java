package com.systemdesign.pastebin.service;

import com.systemdesign.pastebin.idgen.SnowflakeIdGenerator;
import com.systemdesign.pastebin.domain.Paste;
import com.systemdesign.pastebin.domain.PasteVisibility;
import com.systemdesign.pastebin.repository.PasteRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

/**
 * Paste oluşturma, okuma ve TTL temizliği iş kurallarını yürüten application service.
 * <p>
 * System design kavramı: <b>Pastebin core logic</b> — metin içeriğini unique id ile
 * DB'ye yazar; opsiyonel TTL ile otomatik expire sağlar; read path'te expire kontrolü yapar.
 * <p>
 * {@link SnowflakeIdGenerator} ile dağıtık ortamda çakışmasız id üretir;
 * {@link PasteRepository} ile persistence işlemlerini yürütür.
 */
@Service
public class PasteService {

    private final PasteRepository pasteRepository;
    private final SnowflakeIdGenerator idGenerator;

    public PasteService(PasteRepository pasteRepository, SnowflakeIdGenerator idGenerator) {
        this.pasteRepository = pasteRepository;
        this.idGenerator = idGenerator;
    }

    /**
     * Yeni paste kaydı oluşturur; TTL verilmezse süresiz, visibility verilmezse PUBLIC olur.
     *
     * @param content   saklanacak metin
     * @param ttl       opsiyonel yaşam süresi; null ise expire olmaz
     * @param visibility erişim seviyesi; null ise {@link PasteVisibility#PUBLIC}
     * @return kaydedilen {@link Paste} entity
     * @throws IllegalArgumentException content boşsa
     */
    @Transactional
    public Paste create(String content, Duration ttl, PasteVisibility visibility) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("content boş olamaz");
        }
        PasteVisibility effectiveVisibility = visibility == null ? PasteVisibility.PUBLIC : visibility;
        Instant now = Instant.now();
        Instant expiresAt = ttl == null ? null : now.plus(ttl);
        String id = Long.toString(idGenerator.nextId());
        return pasteRepository.save(new Paste(id, content, expiresAt, effectiveVisibility, now));
    }

    /**
     * Id ile paste arar; süresi dolmuş kayıtları filtreler (lazy expire check).
     *
     * @param id aranan paste id
     * @return geçerli paste veya boş {@link Optional}
     */
    @Transactional(readOnly = true)
    public Optional<Paste> findById(String id) {
        return pasteRepository.findById(id).filter(paste -> !paste.isExpired(Instant.now()));
    }

    /**
     * Süresi dolmuş paste kayıtlarını DB'den siler — scheduled background cleanup.
     * Her 60 saniyede bir çalışır; storage maliyetini ve tablo boyutunu kontrol altında tutar.
     */
    @Transactional
    @Scheduled(fixedRate = 60_000)
    public void cleanupExpired() {
        pasteRepository.deleteExpired(Instant.now());
    }
}
