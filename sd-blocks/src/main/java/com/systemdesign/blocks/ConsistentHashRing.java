package com.systemdesign.blocks;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Consistent hash ring — key'leri node'lara deterministik ve dengeli dağıtır.
 * <p>
 * Her fiziksel node için {@code virtualNodes} kadar sanal node hash ring'e yerleştirilir;
 * bu sayede node ekleme/çıkarma sırasında yalnızca komşu key'ler etkilenir (minimal remapping).
 * Key lookup: key hash'i ring'de saat yönünde ilk node'u bulur ({@code ceilingEntry}).
 */
public final class ConsistentHashRing {

    /** Hash değeri → fiziksel node adı; sıralı map ile O(log n) lookup sağlar. */
    private final TreeMap<Long, String> ring = new TreeMap<>();

    /**
     * Verilen node listesi ve virtual node sayısı ile hash ring oluşturur.
     *
     * @param nodes        fiziksel node adları (boş olamaz)
     * @param virtualNodes her node için ring'e yerleştirilecek sanal node sayısı
     * @throws IllegalArgumentException nodes boş veya virtualNodes geçersiz ise
     */
    public ConsistentHashRing(List<String> nodes, int virtualNodes) {
        if (nodes == null || nodes.isEmpty()) {
            throw new IllegalArgumentException("nodes boş olamaz");
        }
        if (virtualNodes <= 0) {
            throw new IllegalArgumentException("virtualNodes pozitif olmalı");
        }
        // Her fiziksel node için virtualNodes kadar sanal nokta ring'e ekle
        for (String node : nodes) {
            for (int i = 0; i < virtualNodes; i++) {
                ring.put(hash(node + "#" + i), node);
            }
        }
    }

    /**
     * Verilen key'in atanacağı fiziksel node'u döner.
     * <p>
     * Consistent hash lookup: key hash'inden büyük/eşit ilk ring noktasını bul;
     * ring sonunu geçtiyse en küçük noktaya wrap-around yap.
     *
     * @param key dağıtılacak key (ör. user id, cache key)
     * @return sorumlu node adı
     * @throws IllegalArgumentException key null veya blank ise
     */
    public String getNode(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("key boş olamaz");
        }
        long keyHash = hash(key);
        // ceilingEntry: keyHash'ten büyük veya eşit ilk ring noktası
        Map.Entry<Long, String> entry = ring.ceilingEntry(keyHash);
        if (entry == null) {
            // Wrap-around: ring sonunu geçtiyse en küçük hash noktasına dön
            entry = ring.firstEntry();
        }
        return entry.getValue();
    }

    /**
     * SHA-256 digest'in ilk 8 byte'ını unsigned long hash değerine çevirir.
     *
     * @param value hash'lenecek string
     * @return 64-bit hash değeri
     */
    static long hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            long result = 0;
            for (int i = 0; i < 8; i++) {
                result = (result << 8) | (bytes[i] & 0xffL);
            }
            return result;
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 unavailable", ex);
        }
    }
}
