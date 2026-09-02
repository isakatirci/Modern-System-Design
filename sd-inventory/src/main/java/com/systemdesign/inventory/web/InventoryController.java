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

/**
 * Envanter ve koltuk rezervasyonu için REST API katmanı.
 * <p>
 * HTTP request'leri ilgili domain service'lere yönlendirir:
 * {@link InventoryService} (CAS stok) ve {@link SeatReservationService} (hold + TTL).
 */
@RestController
@RequestMapping("/api/v1")
public class InventoryController {

    private final InventoryService inventoryService;
    private final SeatReservationService seatReservationService;

    /**
     * Envanter ve rezervasyon servislerini inject eder.
     *
     * @param inventoryService       CAS tabanlı stok servisi
     * @param seatReservationService TTL'li koltuk hold servisi
     */
    public InventoryController(InventoryService inventoryService, SeatReservationService seatReservationService) {
        this.inventoryService = inventoryService;
        this.seatReservationService = seatReservationService;
    }

    /**
     * SKU için başlangıç stok miktarını tanımlar.
     *
     * @param sku      ürün kodu
     * @param quantity başlangıç adedi
     * @return sku ve available bilgisi
     */
    @PostMapping("/inventory/{sku}/init")
    public Map<String, Object> init(@PathVariable String sku, @RequestParam int quantity) {
        inventoryService.initStock(sku, quantity);
        return Map.of("sku", sku, "available", quantity);
    }

    /**
     * Stoktan bir birim satın almayı dener (CAS decrement).
     *
     * @param sku hedef ürün kodu
     * @return success durumu ve kalan stok
     */
    @PostMapping("/inventory/{sku}/purchase")
    public Map<String, Object> purchase(@PathVariable String sku) {
        boolean success = inventoryService.tryDecrement(sku);
        return Map.of("success", success, "remaining", inventoryService.available(sku));
    }

    /**
     * Etkinlik koltuğunu kullanıcı adına geçici hold eder.
     *
     * @param eventId etkinlik id
     * @param seatId  koltuk id
     * @param userId  hold isteyen kullanıcı id
     * @return hold başarı durumu
     */
    @PostMapping("/events/{eventId}/seats/{seatId}/hold")
    public Map<String, Object> hold(
            @PathVariable String eventId,
            @PathVariable String seatId,
            @RequestParam String userId) {
        boolean held = seatReservationService.tryHold(eventId, seatId, userId);
        return Map.of("held", held);
    }

    /**
     * Koltuğun aktif hold sahibini sorgular.
     *
     * @param eventId etkinlik id
     * @param seatId  koltuk id
     * @return userId varsa döner, hold yoksa boş map
     */
    @GetMapping("/events/{eventId}/seats/{seatId}/holder")
    public Map<String, String> holder(@PathVariable String eventId, @PathVariable String seatId) {
        return seatReservationService.currentHolder(eventId, seatId)
                .map(userId -> Map.of("userId", userId))
                .orElse(Map.of());
    }
}
