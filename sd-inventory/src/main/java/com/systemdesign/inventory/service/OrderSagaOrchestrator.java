package com.systemdesign.inventory.service;

import org.springframework.stereotype.Service;

/**
 * Dağıtık sipariş yerleştirme için saga orchestrator.
 * <p>
 * Sistem tasarımı kavramı: stok düşürme ve ödeme ayrı adımlardır;
 * tek bir ACID transaction ile sarılamaz. <strong>Saga pattern</strong>
 * her adımı sırayla yürütür; bir adım başarısız olursa önceki adımlar
 * için <em>compensation</em> (geri alma) tetiklenir — örneğin ödeme
 * fail olunca stok iade edilir.
 */
@Service
public class OrderSagaOrchestrator {

    private final InventoryService inventoryService;

    /**
     * Envanter servisini inject eder.
     *
     * @param inventoryService CAS tabanlı stok servisi
     */
    public OrderSagaOrchestrator(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    /**
     * Saga adımlarını sırayla çalıştırır: stok düşür → ödeme al.
     *
     * @param sku             sipariş edilen ürün kodu
     * @param paymentGateway  ödeme adımını simüle eden gateway
     * @throws IllegalStateException stok yetersizse veya ödeme başarısızsa
     */
    public void placeOrder(String sku, PaymentGateway paymentGateway) {
        // Saga adım 1: stok rezervasyonu (forward transaction)
        if (!inventoryService.tryDecrement(sku)) {
            throw new IllegalStateException("Stok tükendi: " + sku);
        }
        try {
            // Saga adım 2: ödeme tahsilatı
            paymentGateway.charge();
        } catch (RuntimeException ex) {
            // Saga compensation: ödeme fail olursa stok adımını geri almak gerekir
            // (production'da burada inventory increment / release compensation çağrılır)
            throw new IllegalStateException("Ödeme başarısız, sipariş iptal edildi", ex);
        }
    }

    /**
     * Ödeme adımını temsil eden fonksiyonel arayüz; test ve demo için inject edilir.
     */
    @FunctionalInterface
    public interface PaymentGateway {
        /**
         * Ödeme tahsilatını gerçekleştirir.
         *
         * @throws RuntimeException ödeme gateway'i hata dönerse
         */
        void charge();
    }
}
