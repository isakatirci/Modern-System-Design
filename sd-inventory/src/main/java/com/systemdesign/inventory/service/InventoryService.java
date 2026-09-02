package com.systemdesign.inventory.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/** Flash sale için atomic inventory decrement. */
@Service
public class InventoryService {

    private final Map<String, AtomicInteger> stockBySku = new ConcurrentHashMap<>();

    public void initStock(String sku, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("quantity negatif olamaz");
        }
        stockBySku.put(sku, new AtomicInteger(quantity));
    }

    public boolean tryDecrement(String sku) {
        AtomicInteger stock = stockBySku.get(sku);
        if (stock == null) {
            throw new IllegalArgumentException("SKU bulunamadı: " + sku);
        }
        while (true) {
            int current = stock.get();
            if (current <= 0) {
                return false;
            }
            if (stock.compareAndSet(current, current - 1)) {
                return true;
            }
        }
    }

    public int available(String sku) {
        AtomicInteger stock = stockBySku.get(sku);
        return stock == null ? 0 : stock.get();
    }
}
