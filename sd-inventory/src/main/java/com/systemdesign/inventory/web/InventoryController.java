package com.systemdesign.inventory.web;

import com.systemdesign.inventory.service.InventoryService;
import com.systemdesign.inventory.service.SeatReservationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class InventoryController {

    private final InventoryService inventoryService;
    private final SeatReservationService seatReservationService;

    public InventoryController(InventoryService inventoryService, SeatReservationService seatReservationService) {
        this.inventoryService = inventoryService;
        this.seatReservationService = seatReservationService;
    }

    @PostMapping("/inventory/{sku}/init")
    public Map<String, Object> init(@PathVariable String sku, @RequestParam int quantity) {
        inventoryService.initStock(sku, quantity);
        return Map.of("sku", sku, "available", quantity);
    }

    @PostMapping("/inventory/{sku}/purchase")
    public Map<String, Object> purchase(@PathVariable String sku) {
        boolean success = inventoryService.tryDecrement(sku);
        return Map.of("success", success, "remaining", inventoryService.available(sku));
    }

    @PostMapping("/events/{eventId}/seats/{seatId}/hold")
    public Map<String, Object> hold(
            @PathVariable String eventId,
            @PathVariable String seatId,
            @RequestParam String userId) {
        boolean held = seatReservationService.tryHold(eventId, seatId, userId);
        return Map.of("held", held);
    }

    @GetMapping("/events/{eventId}/seats/{seatId}/holder")
    public Map<String, String> holder(@PathVariable String eventId, @PathVariable String seatId) {
        return seatReservationService.currentHolder(eventId, seatId)
                .map(userId -> Map.of("userId", userId))
                .orElse(Map.of());
    }
}
