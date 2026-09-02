package com.systemdesign.ratelimit.service;

import com.systemdesign.ratelimit.limiter.TokenBucketRateLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Client bazlı rate limiting servisi.
 * <p>
 * Her {@code clientId} için ayrı bir {@code TokenBucketRateLimiter} tutar;
 * böylece bir client'ın limiti diğerlerini etkilemez.
 */
@Service
public class RateLimitService {

    private final double capacity;
    private final double refillPerSecond;
    /** clientId → kendi token bucket limiter'ı (lazy oluşturulur). */
    private final Map<String, TokenBucketRateLimiter> limiters = new ConcurrentHashMap<>();

    /**
     * application.properties'ten capacity ve refill rate değerlerini okur.
     *
     * @param capacity        bucket kapasitesi (burst limit)
     * @param refillPerSecond saniyede eklenen token miktarı
     */
    public RateLimitService(
            @Value("${systemdesign.ratelimit.capacity}") double capacity,
            @Value("${systemdesign.ratelimit.refill-per-second}") double refillPerSecond) {
        this.capacity = capacity;
        this.refillPerSecond = refillPerSecond;
    }

    /**
     * Verilen client için isteğe izin verilip verilmeyeceğini kontrol eder.
     * <p>
     * İlk istekte client için yeni bir token bucket oluşturulur (computeIfAbsent).
     *
     * @param clientId isteği yapan client tanımlayıcısı
     * @return rate limit aşılmadıysa true
     */
    public boolean allow(String clientId) {
        TokenBucketRateLimiter limiter = limiters.computeIfAbsent(
                clientId, key -> new TokenBucketRateLimiter(capacity, refillPerSecond));
        return limiter.tryAcquire();
    }
}
