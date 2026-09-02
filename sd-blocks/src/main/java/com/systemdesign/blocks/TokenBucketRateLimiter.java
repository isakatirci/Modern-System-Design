package com.systemdesign.blocks;

/**
 * Token bucket rate limiter — kısa süreli burst traffic'e izin verirken
 * uzun vadede ortalama istek hızını sınırlar.
 * <p>
 * Bucket'ta {@code capacity} kadar token birikir; her istek 1 token harcar.
 * Zaman geçtikçe {@code refillRatePerSecond} hızında token eklenir.
 * Token yoksa istek reddedilir.
 */
public final class TokenBucketRateLimiter {

    /** Bucket'ın taşıyabileceği maksimum token sayısı (burst limit). */
    private final double capacity;
    /** Saniyede bucket'a eklenen token miktarı (sürekli rate limit). */
    private final double refillRatePerSecond;
    /** Mevcut kullanılabilir token sayısı. */
    private double tokens;
    /** Son refill hesabının yapıldığı zaman (nanos). */
    private long lastRefillNanos;

    /**
     * Yeni bir token bucket oluşturur; başlangıçta bucket tam dolu olur.
     *
     * @param capacity             maksimum token kapasitesi (burst boyutu)
     * @param refillRatePerSecond  saniyede eklenen token miktarı
     * @throws IllegalArgumentException capacity veya refillRate sıfır/negatif ise
     */
    public TokenBucketRateLimiter(double capacity, double refillRatePerSecond) {
        if (capacity <= 0 || refillRatePerSecond <= 0) {
            throw new IllegalArgumentException("capacity ve refillRate pozitif olmalı");
        }
        this.capacity = capacity;
        this.refillRatePerSecond = refillRatePerSecond;
        this.tokens = capacity;
        this.lastRefillNanos = System.nanoTime();
    }

    /**
     * 1 token harcamayı dener; yeterli token varsa true döner.
     * <p>
     * Thread-safe: aynı bucket'a eşzamanlı erişim synchronized ile korunur.
     *
     * @return token alındıysa true, aksi halde false (rate limit aşıldı)
     */
    public synchronized boolean tryAcquire() {
        refill();
        if (tokens >= 1.0d) {
            tokens -= 1.0d;
            return true;
        }
        return false;
    }

    /**
     * Geçen süreye göre token bucket'ı doldurur.
     * Token sayısı capacity'yi aşamaz — fazla token birikmez.
     */
    private void refill() {
        long now = System.nanoTime();
        double elapsedSeconds = (now - lastRefillNanos) / 1_000_000_000.0d;
        // elapsed * refillRate kadar token ekle, capacity ile sınırla
        tokens = Math.min(capacity, tokens + elapsedSeconds * refillRatePerSecond);
        lastRefillNanos = now;
    }
}
