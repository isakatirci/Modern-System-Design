package com.systemdesign.blocks;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** Consistent hash ring; virtual node ile yük dengesi sağlanır. */
public final class ConsistentHashRing {

    private final TreeMap<Long, String> ring = new TreeMap<>();

    public ConsistentHashRing(List<String> nodes, int virtualNodes) {
        if (nodes == null || nodes.isEmpty()) {
            throw new IllegalArgumentException("nodes boş olamaz");
        }
        if (virtualNodes <= 0) {
            throw new IllegalArgumentException("virtualNodes pozitif olmalı");
        }
        for (String node : nodes) {
            for (int i = 0; i < virtualNodes; i++) {
                ring.put(hash(node + "#" + i), node);
            }
        }
    }

    public String getNode(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("key boş olamaz");
        }
        long keyHash = hash(key);
        Map.Entry<Long, String> entry = ring.ceilingEntry(keyHash);
        if (entry == null) {
            entry = ring.firstEntry();
        }
        return entry.getValue();
    }

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
