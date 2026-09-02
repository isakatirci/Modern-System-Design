package com.systemdesign.idgen;

/**
 * Twitter Snowflake ID generator — dağıtık ortamda sıralı, benzersiz 64-bit ID üretir.
 * <p>
 * ID yapısı (soldan sağa, toplam 64 bit):
 * <ul>
 *   <li>41 bit timestamp (custom epoch'tan itibaren ms)</li>
 *   <li>10 bit machineId (0–1023, worker/process tanımlayıcısı)</li>
 *   <li>12 bit sequence (aynı ms içindeki sıra numarası, 0–4095)</li>
 * </ul>
 * Aynı milisaniyede 4096'dan fazla ID gerekirse bir sonraki ms beklenir.
 */
public final class SnowflakeIdGenerator {

    /** Snowflake custom epoch: 2020-11-04 UTC (Twitter'ın kullandığı epoch). */
    private static final long EPOCH_MS = 1_609_459_200_000L;
    /** machineId alanı bit genişliği. */
    private static final long MACHINE_BITS = 10L;
    /** sequence alanı bit genişliği. */
    private static final long SEQUENCE_BITS = 12L;
    /** Aynı ms içinde üretilebilecek maksimum sequence (4095). */
    private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1;
    /** machineId'nin bit-shift offset'i (sequence'den sonra). */
    private static final long MACHINE_SHIFT = SEQUENCE_BITS;
    /** timestamp'in bit-shift offset'i (sequence + machineId sonrası). */
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + MACHINE_BITS;

    /** Bu generator instance'ına atanmış worker/machine kimliği. */
    private final long machineId;
    /** Son ID üretiminde kullanılan timestamp (ms). */
    private long lastTimestamp = -1L;
    /** Aynı ms içindeki artan sıra numarası. */
    private long sequence = 0L;

    /**
     * Belirli bir machineId ile Snowflake generator oluşturur.
     *
     * @param machineId worker tanımlayıcısı (0–1023 arası, 10 bit)
     * @throws IllegalArgumentException machineId aralık dışındaysa
     */
    public SnowflakeIdGenerator(long machineId) {
        if (machineId < 0 || machineId >= (1L << MACHINE_BITS)) {
            throw new IllegalArgumentException("machineId 0-1023 aralığında olmalı");
        }
        this.machineId = machineId;
    }

    /**
     * Bir sonraki benzersiz Snowflake ID'yi üretir.
     * <p>
     * Thread-safe: aynı generator'a eşzamanlı erişim synchronized ile korunur.
     *
     * @return 64-bit benzersiz, zaman sıralı ID
     * @throws IllegalStateException sistem saati geri giderse (clock skew)
     */
    public synchronized long nextId() {
        long timestamp = currentTimeMillis();
        // Clock skew koruması: saat geri giderse duplicate ID riski — fail fast
        if (timestamp < lastTimestamp) {
            throw new IllegalStateException("Clock moved backwards");
        }
        if (timestamp == lastTimestamp) {
            // Aynı ms: sequence'ı artır; 4096'ya ulaşınca bir sonraki ms'yi bekle
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                timestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            // Yeni ms: sequence sıfırdan başlar
            sequence = 0;
        }
        lastTimestamp = timestamp;
        // Snowflake bit packing: timestamp | machineId | sequence
        return ((timestamp - EPOCH_MS) << TIMESTAMP_SHIFT) | (machineId << MACHINE_SHIFT) | sequence;
    }

    /** Sequence taşana kadar bir sonraki milisaniyeyi busy-wait ile bekler. */
    private long waitNextMillis(long lastTs) {
        long ts = currentTimeMillis();
        while (ts <= lastTs) {
            ts = currentTimeMillis();
        }
        return ts;
    }

    /** Test edilebilirlik için ayrılmış; production'da System.currentTimeMillis kullanır. */
    long currentTimeMillis() {
        return System.currentTimeMillis();
    }
}
