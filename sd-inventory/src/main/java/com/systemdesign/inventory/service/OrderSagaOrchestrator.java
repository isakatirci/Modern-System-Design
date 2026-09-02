package com.systemdesign.inventory.service;

import org.springframework.stereotype.Service;

/** Saga pattern ile order placement; payment fail olursa inventory release edilir. */
@Service
public class OrderSagaOrchestrator {

    private final InventoryService inventoryService;

    public OrderSagaOrchestrator(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    public void placeOrder(String sku, PaymentGateway paymentGateway) {
        if (!inventoryService.tryDecrement(sku)) {
            throw new IllegalStateException("Stok tükendi: " + sku);
        }
        try {
            paymentGateway.charge();
        } catch (RuntimeException ex) {
            throw new IllegalStateException("Ödeme başarısız, sipariş iptal edildi", ex);
        }
    }

    @FunctionalInterface
    public interface PaymentGateway {
        void charge();
    }
}
