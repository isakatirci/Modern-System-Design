package com.systemdesign.shorturl.service;

import com.systemdesign.shorturl.domain.ShortUrlKey;
import com.systemdesign.shorturl.repository.ShortUrlKeyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class KeyGenerationService {

    private final ShortUrlKeyRepository keyRepository;

    public KeyGenerationService(ShortUrlKeyRepository keyRepository) {
        this.keyRepository = keyRepository;
    }

    @Transactional
    public String allocateKey() {
        ShortUrlKey key = keyRepository.findFirstByAllocatedFalseOrderByIdAsc()
                .orElseThrow(() -> new IllegalStateException("Kullanılabilir short key kalmadı"));
        key.setAllocated(true);
        keyRepository.save(key);
        return key.getKeyValue();
    }

    @Transactional
    public void preAllocateKeys(int count, long startId) {
        List<ShortUrlKey> keys = new ArrayList<>(count);
        for (long i = 0; i < count; i++) {
            keys.add(new ShortUrlKey(Base62Encoder.encode(startId + i), false));
        }
        keyRepository.saveAll(keys);
    }
}
