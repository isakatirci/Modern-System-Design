package com.systemdesign.inventory.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * SKU bazlı stok yönetimi; flash sale senaryosu için tasarlanmıştır.
 * <p>
 * Sistem tasarımı kavramı: yüksek eşzamanlılıkta klasik read-modify-write
 * race condition üretir. Bu servis {@link AtomicInteger#compareAndSet} ile
 * lock-free <strong>CAS (Compare-And-Swap)</strong> döngüsü kullanarak stok
 * azaltmayı atomik ve thread-safe yapar.
 */
@Service
public class InventoryService {

    /** SKU → kalan stok; her SKU için ayrı AtomicInteger counter tutulur. */
    private final Map<String, AtomicInteger> stockBySku = new ConcurrentHashMap<>();

    /**
     * Bir SKU için başlangıç stok miktarını ayarlar.
     *
     * @param sku      ürün kodu
     * @param quantity başlangıç adedi (negatif olamaz)
     * @throws IllegalArgumentException quantity negatifse
     */
    public void initStock(String sku, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("quantity negatif olamaz");
        }
        stockBySku.put(sku, new AtomicInteger(quantity));
    }

    /**
     * Stoktan bir birim düşmeyi dener; CAS döngüsü ile atomik azaltma yapar.
     *
     * @param sku hedef ürün kodu
     * @return stok yeterliyse {@code true}, tükendiyse {@code false}
     * @throws IllegalArgumentException SKU kayıtlı değilse
     */
    public boolean tryDecrement(String sku) {
        AtomicInteger stock = stockBySku.get(sku);
        if (stock == null) {
            throw new IllegalArgumentException("SKU bulunamadı: " + sku);
        }
        // CAS inventory loop: başarılı swap olana kadar oku-karşılaştır-yaz tekrarla
        while (true) {
            int current = stock.get();
            if (current <= 0) {
                return false;
            }
            // compareAndSet başarısız olursa başka thread araya girmiştir; döngü yeniden dener
            if (stock.compareAndSet(current, current - 1)) {
                return true;
            }
        }
    }

    /**
     * SKU için kalan stok miktarını döner.
     *
     * @param sku sorgulanacak ürün kodu
     * @return kalan adet; SKU yoksa 0
     */
    public int available(String sku) {
        AtomicInteger stock = stockBySku.get(sku);
        return stock == null ? 0 : stock.get();
    }
}
