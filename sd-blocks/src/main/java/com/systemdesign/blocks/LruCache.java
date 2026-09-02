package com.systemdesign.blocks;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/** Thread-safe LRU cache; eviction policy olarak LinkedHashMap access-order kullanır. */
public final class LruCache<K, V> {

    private final int maxSize;
    private final Map<K, V> store;

    public LruCache(int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize pozitif olmalı");
        }
        this.maxSize = maxSize;
        this.store = new LinkedHashMap<>(maxSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > LruCache.this.maxSize;
            }
        };
    }

    public synchronized Optional<V> get(K key) {
        return Optional.ofNullable(store.get(key));
    }

    public synchronized V getOrLoad(K key, Function<K, V> loader) {
        return store.computeIfAbsent(key, loader);
    }

    public synchronized void put(K key, V value) {
        store.put(key, value);
    }

    public synchronized int size() {
        return store.size();
    }
}
