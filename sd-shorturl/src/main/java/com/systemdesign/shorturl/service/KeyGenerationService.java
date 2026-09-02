package com.systemdesign.shorturl.service;

import com.systemdesign.shorturl.domain.ShortUrlKey;
import com.systemdesign.shorturl.repository.ShortUrlKeyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Short key üretimi ve havuz yönetimi — Key Generation Service (KGS) implementasyonu.
 * <p>
 * System design kavramı: <b>KGS / pre-allocated key pool</b> — short key'ler create
 * request'i sırasında değil, önceden batch olarak üretilip DB'de bekletilir.
 * Create path sadece havuzdan bir key "allocate" eder; bu sayede ID üretimi write path'ten ayrılır.
 * <p>
 * {@link ShortUrlKeyRepository} ile key havuzunu okur/yazar;
 * {@link Base62Encoder} ile sayısal id'leri kısa string'e çevirir.
 */
@Service
public class KeyGenerationService {

    private final ShortUrlKeyRepository keyRepository;

    public KeyGenerationService(ShortUrlKeyRepository keyRepository) {
        this.keyRepository = keyRepository;
    }

    /**
     * Havuzdan henüz kullanılmamış ilk key'i alır ve allocated olarak işaretler.
     *
     * @return kullanıma hazır short key string'i
     * @throws IllegalStateException havuzda boş key kalmadıysa
     */
    @Transactional
    public String allocateKey() {
        ShortUrlKey key = keyRepository.findFirstByAllocatedFalseOrderByIdAsc()
                .orElseThrow(() -> new IllegalStateException("Kullanılabilir short key kalmadı"));
        key.setAllocated(true);
        keyRepository.save(key);
        return key.getKeyValue();
    }

    /**
     * Belirtilen sayıda short key'i önceden üretip havuza ekler (KGS pre-allocation).
     * <p>
     * {@code startId} ile başlayan ardışık sayılar Base62'ye encode edilir; her biri
     * {@code allocated=false} olarak kaydedilir ve create sırasında tüketilir.
     *
     * @param count üretilecek key adedi
     * @param startId Base62 encode için başlangıç sayısal id
     */
    @Transactional
    public void preAllocateKeys(int count, long startId) {
        List<ShortUrlKey> keys = new ArrayList<>(count);
        for (long i = 0; i < count; i++) {
            // Sıralı sayısal id → Base62 string; collision riski düşük, kısa URL dostu
            keys.add(new ShortUrlKey(Base62Encoder.encode(startId + i), false));
        }
        keyRepository.saveAll(keys);
    }
}
