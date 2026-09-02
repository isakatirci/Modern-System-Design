package com.systemdesign.common;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Kubernetes readiness probe için warmup state kontrolü.
 * <p>
 * Uygulama henüz hazır değilken (warmup, cache doldurma vb.)
 * {@code markNotReady()} ile DOWN döner; trafik yönlendirilmez.
 */
@Component("systemReadinessIndicator")
public class SystemReadinessHealthIndicator implements HealthIndicator {

    /** Thread-safe readiness flag; false iken health DOWN döner. */
    private final AtomicBoolean ready = new AtomicBoolean(true);

    /**
     * Actuator health endpoint'inin sorguladığı readiness durumu.
     *
     * @return UP (hazır) veya DOWN (warmup devam ediyor)
     */
    @Override
    public Health health() {
        if (!ready.get()) {
            return Health.down().withDetail("reason", "warmup in progress").build();
        }
        return Health.up().build();
    }

    /** Uygulamayı hazır olarak işaretler; readiness probe UP döner. */
    public void markReady() {
        ready.set(true);
    }

    /** Uygulamayı hazır değil olarak işaretler; readiness probe DOWN döner. */
    public void markNotReady() {
        ready.set(false);
    }
}
