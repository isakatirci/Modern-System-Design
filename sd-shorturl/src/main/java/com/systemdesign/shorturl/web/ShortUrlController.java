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

@RestController
public class ShortUrlController {

    private final UrlShortenerService urlShortenerService;
    private final KeyGenerationService keyGenerationService;

    public ShortUrlController(UrlShortenerService urlShortenerService, KeyGenerationService keyGenerationService) {
        this.urlShortenerService = urlShortenerService;
        this.keyGenerationService = keyGenerationService;
    }

    @PostConstruct
    void seedKeys() {
        keyGenerationService.preAllocateKeys(1000, 1L);
    }

    @PostMapping("/api/v1/urls")
    public ShortUrlResponse create(@Valid @RequestBody CreateShortUrlRequest request) {
        ShortenedUrl shortened = urlShortenerService.shorten(request.longUrl());
        return new ShortUrlResponse(
                shortened.getShortKey(),
                urlShortenerService.buildShortUrl(shortened.getShortKey()),
                shortened.getLongUrl());
    }

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
