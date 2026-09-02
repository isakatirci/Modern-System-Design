package com.systemdesign.shorturl.web;

import com.systemdesign.shorturl.domain.ShortenedUrl;
import com.systemdesign.shorturl.service.KeyGenerationService;
import com.systemdesign.shorturl.service.UrlShortenerService;
import com.systemdesign.shorturl.web.dto.CreateShortUrlRequest;
import com.systemdesign.shorturl.web.dto.ShortUrlResponse;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * URL kısaltma servisinin REST API katmanı.
 * <p>
 * System design kavramı: <b>API Gateway / Web Layer</b> — HTTP request'leri alır,
 * validation uygular ve domain service'lere delege eder; response ve redirect döner.
 * <p>
 * {@link UrlShortenerService} ile shorten/resolve işlemlerini yapar;
 * {@link KeyGenerationService} ile uygulama açılışında key havuzunu doldurur (KGS pre-allocation).
 */
@RestController
public class ShortUrlController {

    private final UrlShortenerService urlShortenerService;
    private final KeyGenerationService keyGenerationService;

    public ShortUrlController(UrlShortenerService urlShortenerService, KeyGenerationService keyGenerationService) {
        this.urlShortenerService = urlShortenerService;
        this.keyGenerationService = keyGenerationService;
    }

    /**
     * Uygulama başlarken key havuzuna önceden üretilmiş short key'leri yükler.
     * KGS (Key Generation Service) pattern: create sırasında DB'de key üretmek yerine
     * havuzdan hazır key çekilir; bu da latency ve contention'ı azaltır.
     */
    @PostConstruct
    void seedKeys() {
        keyGenerationService.preAllocateKeys(1000, 1L);
    }

    /**
     * Uzun URL'yi kısaltır ve oluşturulan short URL bilgisini döner.
     *
     * @param request {@code longUrl} içeren create request
     * @return short key, tam short URL ve orijinal long URL
     */
    @PostMapping("/api/v1/urls")
    public ShortUrlResponse create(@Valid @RequestBody CreateShortUrlRequest request) {
        ShortenedUrl shortened = urlShortenerService.shorten(request.longUrl());
        return new ShortUrlResponse(
                shortened.getShortKey(),
                urlShortenerService.buildShortUrl(shortened.getShortKey()),
                shortened.getLongUrl());
    }

    /**
     * Short key ile gelen isteği orijinal long URL'ye HTTP 302 redirect eder.
     *
     * @param shortKey path'teki kısa anahtar
     * @return bulunursa {@code Location} header'lı redirect; yoksa 404
     */
    @GetMapping("/{shortKey}")
    public ResponseEntity<Void> redirect(@PathVariable String shortKey) {
        Optional<String> longUrl = urlShortenerService.resolveLongUrl(shortKey);
        if (longUrl.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, longUrl.get())
                .build();
    }
}
