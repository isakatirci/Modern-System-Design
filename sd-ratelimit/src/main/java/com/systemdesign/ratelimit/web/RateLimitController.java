package com.systemdesign.ratelimit.web;

import com.systemdesign.ratelimit.service.RateLimitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class RateLimitController {

    private final RateLimitService rateLimitService;

    public RateLimitController(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

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
