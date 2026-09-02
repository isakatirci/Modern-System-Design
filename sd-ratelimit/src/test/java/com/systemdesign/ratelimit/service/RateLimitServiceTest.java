package com.systemdesign.ratelimit.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class RateLimitServiceTest {

    @Autowired
    private RateLimitService rateLimitService;

    @Test
    void blocksAfterCapacity() {
        String client = "client-" + System.nanoTime();
        int allowed = 0;
        for (int i = 0; i < 20; i++) {
            if (rateLimitService.allow(client)) {
                allowed++;
            }
        }
        assertTrue(allowed <= 10);
    }
}
