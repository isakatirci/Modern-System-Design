package com.systemdesign.pastebin.idgen;

/**
 * Twitter Snowflake algoritması ile dağıtık, monoton artan unique id üretir.
 * <p>
 * System design kavramı: <b>Snowflake ID</b> — 64-bit id yapısı:
 * timestamp (ms) + machine id + sequence. Merkezi koordinasyon veya DB sequence
 * olmadan yüksek throughput'ta unique id sağlar; id'ler zamanla sıralı kalır.
 * <p>
 * {@link com.systemdesign.pastebin.config.IdGenConfig} ile Spring bean olarak
 * oluşturulur; {@link com.systemdesign.pastebin.service.PasteService#create} her
 * yeni paste için {@link #nextId()} çağırır.
 */
public final class SnowflakeIdGenerator {

    // Snowflake epoch (2020-12-01); timestamp bit'leri bu noktadan itibaren sayılır
    private static final long EPOCH_MS = 1_609_459_200_000L;
    private static final long MACHINE_BITS = 10L;
    private static final long SEQUENCE_BITS = 12L;
    private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1;
    private static final long MACHINE_SHIFT = SEQUENCE_BITS;
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + MACHINE_BITS;

    private final long machineId;
    private long lastTimestamp = -1L;
    private long sequence = 0L;

    /**
     * Verilen machine id ile generator oluşturur (0–1023 arası, 10 bit).
     *
     * @param machineId bu JVM/instance'a özgü machine id
     * @throws IllegalArgumentException machine id aralık dışındaysa
     */
    public SnowflakeIdGenerator(long machineId) {
        if (machineId < 0 || machineId >= (1L << MACHINE_BITS)) {
            throw new IllegalArgumentException("machineId 0-1023 aralığında olmalı");
        }
        this.machineId = machineId;
    }

    /**
     * Thread-safe şekilde bir sonraki unique id'yi üretir.
     *
     * @return 64-bit Snowflake id (timestamp | machineId | sequence birleşimi)
     * @throws IllegalStateException sistem saati geri giderse
     */
    public synchronized long nextId() {
        long timestamp = currentTimeMillis();
        if (timestamp < lastTimestamp) {
            throw new IllegalStateException("Clock moved backwards");
        }
        if (timestamp == lastTimestamp) {
            // Aynı ms içinde birden fazla id: sequence artır (12 bit, max 4096/ms)
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                // Sequence taştı — bir sonraki milisaniyeyi bekle
                timestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }
        lastTimestamp = timestamp;
        // Bit layout: [timestamp][machineId][sequence]
        return ((timestamp - EPOCH_MS) << TIMESTAMP_SHIFT) | (machineId << MACHINE_SHIFT) | sequence;
    }

    private long waitNextMillis(long lastTs) {
        long ts = currentTimeMillis();
        while (ts <= lastTs) {
            ts = currentTimeMillis();
        }
        return ts;
    }

    long currentTimeMillis() {
        return System.currentTimeMillis();
    }
}
