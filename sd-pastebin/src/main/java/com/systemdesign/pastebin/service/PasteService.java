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

@Service
public class PasteService {

    private final PasteRepository pasteRepository;
    private final SnowflakeIdGenerator idGenerator;

    public PasteService(PasteRepository pasteRepository, SnowflakeIdGenerator idGenerator) {
        this.pasteRepository = pasteRepository;
        this.idGenerator = idGenerator;
    }

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

    @Transactional(readOnly = true)
    public Optional<Paste> findById(String id) {
        return pasteRepository.findById(id).filter(paste -> !paste.isExpired(Instant.now()));
    }

    @Transactional
    @Scheduled(fixedRate = 60_000)
    public void cleanupExpired() {
        pasteRepository.deleteExpired(Instant.now());
    }
}
