package com.systemdesign.common;

/** Back-of-the-envelope kapasite profili. */
public record CapacityProfile(long dailyActiveUsers, int writesPerUserPerDay, int readsPerUserPerDay, int avgObjectBytes) {

    public static final long SECONDS_PER_DAY = 86_400L;

    public double averageWriteQps() {
        return (double) dailyActiveUsers * writesPerUserPerDay / SECONDS_PER_DAY;
    }

    public double averageReadQps() {
        return (double) dailyActiveUsers * readsPerUserPerDay / SECONDS_PER_DAY;
    }

    public double peakReadQps(double multiplier) {
        return averageReadQps() * multiplier;
    }

    public double dailyStorageGigabytes() {
        long totalBytes = dailyActiveUsers * writesPerUserPerDay * avgObjectBytes;
        return (double) totalBytes / (1024 * 1024 * 1024);
    }
}
