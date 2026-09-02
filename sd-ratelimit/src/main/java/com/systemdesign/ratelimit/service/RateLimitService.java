package com.systemdesign.ratelimit.service;

import com.systemdesign.ratelimit.limiter.TokenBucketRateLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {

    private final double capacity;
    private final double refillPerSecond;
    private final Map<String, TokenBucketRateLimiter> limiters = new ConcurrentHashMap<>();

    public RateLimitService(
            @Value("${systemdesign.ratelimit.capacity}") double capacity,
            @Value("${systemdesign.ratelimit.refill-per-second}") double refillPerSecond) {
        this.capacity = capacity;
        this.refillPerSecond = refillPerSecond;
    }

    public boolean allow(String clientId) {
        TokenBucketRateLimiter limiter = limiters.computeIfAbsent(
                clientId, key -> new TokenBucketRateLimiter(capacity, refillPerSecond));
        return limiter.tryAcquire();
    }
}
