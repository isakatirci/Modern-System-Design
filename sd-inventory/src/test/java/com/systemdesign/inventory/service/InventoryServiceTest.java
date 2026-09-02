package com.systemdesign.inventory.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InventoryServiceTest {

    @Test
    void decrementsAtomically() {
        InventoryService inventory = new InventoryService();
        inventory.initStock("sku-1", 2);
        assertTrue(inventory.tryDecrement("sku-1"));
        assertTrue(inventory.tryDecrement("sku-1"));
        assertFalse(inventory.tryDecrement("sku-1"));
        assertEquals(0, inventory.available("sku-1"));
    }

    @Test
    void sagaRollsBackOnPaymentFailure() {
        InventoryService inventory = new InventoryService();
        inventory.initStock("sku-2", 1);
        OrderSagaOrchestrator saga = new OrderSagaOrchestrator(inventory);
        assertThrows(IllegalStateException.class, () -> saga.placeOrder("sku-2", () -> {
            throw new RuntimeException("payment failed");
        }));
    }
}
