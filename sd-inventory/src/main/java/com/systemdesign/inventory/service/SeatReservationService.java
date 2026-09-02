package com.systemdesign.inventory.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Etkinlik koltuk rezervasyonu; Ticketmaster tarzı geçici hold mekanizması.
 * <p>
 * Sistem tasarımı kavramı: kullanıcı ödeme yapana kadar koltuğu
 * <strong>hold</strong> altında tutar. {@link #holdTtlSeconds} süresi dolunca
 * hold otomatik expire olur ve koltuk tekrar satışa açılır — böylece
 * yarım kalan checkout akışları stok kilitlemez.
 */
@Service
public class SeatReservationService {

    /** Hold süresi (saniye); config'den okunur. */
    private final long holdTtlSeconds;
    /** eventId:seatId → aktif hold kaydı. */
    private final Map<String, Hold> holds = new ConcurrentHashMap<>();

    /**
     * TTL süresini config'den alarak servisi oluşturur.
     *
     * @param holdTtlSeconds hold geçerlilik süresi (saniye)
     */
    public SeatReservationService(@Value("${systemdesign.ticketing.hold-ttl-seconds}") long holdTtlSeconds) {
        this.holdTtlSeconds = holdTtlSeconds;
    }

    /**
     * Koltuğu geçici olarak kullanıcı adına hold eder.
     *
     * @param eventId etkinlik id
     * @param seatId  koltuk id
     * @param userId  hold isteyen kullanıcı id
     * @return hold başarılıysa veya aynı kullanıcının mevcut hold'u varsa {@code true}
     */
    public boolean tryHold(String eventId, String seatId, String userId) {
        cleanupExpired();
        String key = eventId + ":" + seatId;
        Hold existing = holds.get(key);
        // Başka kullanıcının süresi dolmamış hold'u varsa reddet
        if (existing != null && !existing.isExpired()) {
            return existing.userId().equals(userId);
        }
        // Seat hold TTL: şu andan itibaren holdTtlSeconds sonra expire olacak kayıt oluştur
        holds.put(key, new Hold(userId, Instant.now().plusSeconds(holdTtlSeconds)));
        return true;
    }

    /**
     * Kullanıcının hold'unu serbest bırakır (checkout tamamlandı veya iptal).
     *
     * @param eventId etkinlik id
     * @param seatId  koltuk id
     * @param userId  hold sahibi kullanıcı id
     */
    public void release(String eventId, String seatId, String userId) {
        String key = eventId + ":" + seatId;
        holds.computeIfPresent(key, (k, hold) -> hold.userId().equals(userId) ? null : hold);
    }

    /**
     * Koltuğun şu anki hold sahibini döner; expire olmuş hold'lar boş sayılır.
     *
     * @param eventId etkinlik id
     * @param seatId  koltuk id
     * @return aktif hold sahibi userId; hold yoksa empty
     */
    public Optional<String> currentHolder(String eventId, String seatId) {
        cleanupExpired();
        Hold hold = holds.get(eventId + ":" + seatId);
        return hold == null || hold.isExpired() ? Optional.empty() : Optional.of(hold.userId());
    }

    /** TTL süresi dolmuş hold kayıtlarını map'ten temizler. */
    private void cleanupExpired() {
        holds.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    /** Tek bir koltuk hold kaydı; expiresAt anında otomatik geçersiz olur. */
    private record Hold(String userId, Instant expiresAt) {
        boolean isExpired() {
            return Instant.now().isAfter(expiresAt);
        }
    }
}
