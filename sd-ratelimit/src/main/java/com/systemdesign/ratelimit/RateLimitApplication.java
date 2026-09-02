package com.systemdesign.ratelimit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Rate limiting demo uygulamasının Spring Boot entry point'i.
 * <p>
 * Her client için {@code TokenBucketRateLimiter} ile istek hızını sınırlayan
 * HTTP API sunar.
 */
@SpringBootApplication
public class RateLimitApplication {

    /**
     * Uygulamayı başlatır; embedded web server ve Spring context ayağa kalkar.
     *
     * @param args komut satırı argümanları (Spring Boot tarafından parse edilir)
     */
    public static void main(String[] args) {
        SpringApplication.run(RateLimitApplication.class, args);
    }
}
