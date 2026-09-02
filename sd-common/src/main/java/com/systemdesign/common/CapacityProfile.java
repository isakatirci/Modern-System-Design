package com.systemdesign.common;

/**
 * Back-of-the-envelope kapasite profili; sistem tasarımı tahminleri için metrikler.
 * <p>
 * DAU ve kullanıcı başına read/write oranlarından ortalama QPS,
 * peak QPS ve günlük storage ihtiyacı hesaplanır.
 *
 * @param dailyActiveUsers     günlük aktif kullanıcı sayısı
 * @param writesPerUserPerDay  kullanıcı başına günlük write sayısı
 * @param readsPerUserPerDay   kullanıcı başına günlük read sayısı
 * @param avgObjectBytes       ortalama kayıt/object boyutu (byte)
 */
public record CapacityProfile(long dailyActiveUsers, int writesPerUserPerDay, int readsPerUserPerDay, int avgObjectBytes) {

    /** Bir gündeki saniye sayısı; QPS hesaplarında payda olarak kullanılır. */
    public static final long SECONDS_PER_DAY = 86_400L;

    /**
     * Ortalama write QPS (Queries Per Second) değerini hesaplar.
     *
     * @return günlük write'ların saniyeye yayılmış ortalaması
     */
    public double averageWriteQps() {
        return (double) dailyActiveUsers * writesPerUserPerDay / SECONDS_PER_DAY;
    }

    /**
     * Ortalama read QPS değerini hesaplar.
     *
     * @return günlük read'lerin saniyeye yayılmış ortalaması
     */
    public double averageReadQps() {
        return (double) dailyActiveUsers * readsPerUserPerDay / SECONDS_PER_DAY;
    }

    /**
     * Peak read QPS tahmini; ortalama read QPS × peak multiplier.
     *
     * @param multiplier peak saatlerdeki yük çarpanı (ör. 3.0 = 3× ortalama)
     * @return tahmini peak read QPS
     */
    public double peakReadQps(double multiplier) {
        return averageReadQps() * multiplier;
    }

    /**
     * Günlük yeni veri için gereken storage miktarını GB cinsinden hesaplar.
     *
     * @return günlük storage ihtiyacı (gigabyte)
     */
    public double dailyStorageGigabytes() {
        long totalBytes = dailyActiveUsers * writesPerUserPerDay * avgObjectBytes;
        return (double) totalBytes / (1024 * 1024 * 1024);
    }
}
