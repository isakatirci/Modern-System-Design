package com.systemdesign.ratelimit.web;

import com.systemdesign.ratelimit.service.RateLimitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Rate limit korumalı demo REST endpoint'i.
 * <p>
 * {@code X-Client-Id} header'ına göre client bazlı token bucket kontrolü yapar;
 * limit aşılırsa HTTP 429 ve {@code Retry-After} header döner.
 */
@RestController
@RequestMapping("/api/v1")
public class RateLimitController {

    private final RateLimitService rateLimitService;

    /**
     * Spring dependency injection ile rate limit servisini alır.
     *
     * @param rateLimitService client bazlı limiter yönetimi
     */
    public RateLimitController(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    /**
     * Rate limit kontrolünden geçen isteklere başarı yanıtı döner.
     *
     * @param clientId isteği yapan client (header yoksa "anonymous")
     * @return 200 OK veya 429 Too Many Requests
     */
    @GetMapping("/resource")
    public ResponseEntity<Map<String, String>> resource(
            @RequestHeader(value = "X-Client-Id", defaultValue = "anonymous") String clientId) {
        if (!rateLimitService.allow(clientId)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .header("Retry-After", "1")
                    .body(Map.of("error", "rate limit exceeded"));
        }
        return ResponseEntity.ok(Map.of("status", "ok"));
    }
}
