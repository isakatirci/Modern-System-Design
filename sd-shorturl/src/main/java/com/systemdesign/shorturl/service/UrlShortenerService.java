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

    @Transactional
    public ShortenedUrl shorten(String longUrl) {
        validateUrl(longUrl);
        String shortKey = keyGenerationService.allocateKey();
        return urlRepository.save(new ShortenedUrl(shortKey, longUrl, Instant.now()));
    }

    @Cacheable(value = "shortUrls", key = "#shortKey")
    @Transactional(readOnly = true)
    public Optional<String> resolveLongUrl(String shortKey) {
        return urlRepository.findByShortKey(shortKey).map(ShortenedUrl::getLongUrl);
    }

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
