package com.systemdesign.shorturl.service;

import com.systemdesign.shorturl.domain.ShortenedUrl;
import com.systemdesign.shorturl.repository.ShortenedUrlRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.time.Instant;
import java.util.Optional;

/**
 * URL kısaltma ve çözümleme (resolve) iş kurallarını yürüten application service.
 * <p>
 * System design kavramı: <b>URL Shortener core logic</b> — long URL → short key eşlemesini
 * yönetir; okuma tarafında cache ile hot path'i hızlandırır (read-heavy workload).
 * <p>
 * {@link KeyGenerationService} ile unique short key alır,
 * {@link ShortenedUrlRepository} ile eşlemeyi DB'ye yazar/okur.
 */
@Service
public class UrlShortenerService {

    private final ShortenedUrlRepository urlRepository;
    private final KeyGenerationService keyGenerationService;
    private final String baseHost;

    public UrlShortenerService(
            ShortenedUrlRepository urlRepository,
            KeyGenerationService keyGenerationService,
            @Value("${systemdesign.shorturl.base-host}") String baseHost) {
        this.urlRepository = urlRepository;
        this.keyGenerationService = keyGenerationService;
        this.baseHost = baseHost.endsWith("/") ? baseHost.substring(0, baseHost.length() - 1) : baseHost;
    }

    /**
     * Geçerli bir long URL için yeni bir short key üretir ve eşlemeyi kalıcı olarak kaydeder.
     *
     * @param longUrl kısaltılacak orijinal URL
     * @return kaydedilen {@link ShortenedUrl} entity
     * @throws IllegalArgumentException URL boş veya geçersizse
     */
    @Transactional
    public ShortenedUrl shorten(String longUrl) {
        validateUrl(longUrl);
        String shortKey = keyGenerationService.allocateKey();
        return urlRepository.save(new ShortenedUrl(shortKey, longUrl, Instant.now()));
    }

    /**
     * Short key'e karşılık gelen long URL'yi döner; sonuç cache'lenir (redirect hot path).
     *
     * @param shortKey aranan kısa anahtar
     * @return bulunursa long URL; yoksa boş {@link Optional}
     */
    @Cacheable(value = "shortUrls", key = "#shortKey")
    @Transactional(readOnly = true)
    public Optional<String> resolveLongUrl(String shortKey) {
        return urlRepository.findByShortKey(shortKey).map(ShortenedUrl::getLongUrl);
    }

    /**
     * Tam short URL string'ini oluşturur (base host + short key).
     *
     * @param shortKey kısa anahtar
     * @return kullanıcıya verilecek tam short URL
     */
    public String buildShortUrl(String shortKey) {
        return baseHost + "/" + shortKey;
    }

    private void validateUrl(String longUrl) {
        if (longUrl == null || longUrl.isBlank()) {
            throw new IllegalArgumentException("longUrl boş olamaz");
        }
        try {
            URI.create(longUrl);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Geçersiz URL: " + longUrl);
        }
    }
}
