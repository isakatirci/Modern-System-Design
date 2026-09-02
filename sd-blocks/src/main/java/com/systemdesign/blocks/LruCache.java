package com.systemdesign.blocks;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Thread-safe LRU (Least Recently Used) cache.
 * <p>
 * Kapasite aşıldığında en uzun süredir erişilmeyen entry evict edilir.
 * {@code LinkedHashMap} access-order modu ile LRU policy uygulanır.
 *
 * @param <K> cache key tipi
 * @param <V> cache value tipi
 */
public final class LruCache<K, V> {

    /** Cache'in tutabileceği maksimum entry sayısı. */
    private final int maxSize;
    /** Access-order LinkedHashMap; get/put sonrası en son kullanılan sonda kalır. */
    private final Map<K, V> store;

    /**
     * Belirtilen kapasitede LRU cache oluşturur.
     *
     * @param maxSize maksimum entry sayısı
     * @throws IllegalArgumentException maxSize sıfır veya negatif ise
     */
    public LruCache(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize pozitif olmalı");
        }
        this.maxSize = maxSize;
        this.store = new LinkedHashMap<>(maxSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                // LRU eviction: boyut maxSize'ı aşınca en eski (en az kullanılan) entry silinir
                return size() > LruCache.this.maxSize;
            }
        };
    }

    /**
     * Key'e karşılık gelen value'yu döner; yoksa empty Optional.
     * Get işlemi entry'yi "recently used" yapar.
     *
     * @param key aranan key
     * @return value varsa Optional.of, yoksa Optional.empty
     */
    public synchronized Optional<V> get(K key) {
        return Optional.ofNullable(store.get(key));
    }

    /**
     * Key yoksa loader function ile value üretir, cache'e yazar ve döner.
     *
     * @param key    cache key
     * @param loader key bulunamazsa value üreten function
     * @return cache'deki veya yeni yüklenen value
     */
    public synchronized V getOrLoad(K key, Function<K, V> loader) {
        return store.computeIfAbsent(key, loader);
    }

    /**
     * Key-value çiftini cache'e yazar; kapasite aşılırsa LRU eviction tetiklenir.
     *
     * @param key   cache key
     * @param value saklanacak value
     */
    public synchronized void put(K key, V value) {
        store.put(key, value);
    }

    /**
     * Cache'teki mevcut entry sayısını döner.
     *
     * @return entry sayısı
     */
    public synchronized int size() {
        return store.size();
    }
}
