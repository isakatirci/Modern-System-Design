package com.systemdesign.common;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

/** Readiness probe için warmup state kontrolü. */
@Component("systemReadinessIndicator")
public class SystemReadinessHealthIndicator implements HealthIndicator {

    private final AtomicBoolean ready = new AtomicBoolean(true);

    @Override
    public Health health() {
        if (!ready.get()) {
            return Health.down().withDetail("reason", "warmup in progress").build();
        }
        return Health.up().build();
    }

    public void markReady() {
        ready.set(true);
    }

    public void markNotReady() {
        ready.set(false);
    }
}
