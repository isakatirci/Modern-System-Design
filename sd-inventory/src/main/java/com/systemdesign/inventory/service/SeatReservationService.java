package com.systemdesign.inventory.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Ticketmaster tarzı seat hold; TTL sonrası serbest bırakılır. */
@Service
public class SeatReservationService {

    private final long holdTtlSeconds;
    private final Map<String, Hold> holds = new ConcurrentHashMap<>();

    public SeatReservationService(@Value("${systemdesign.ticketing.hold-ttl-seconds}") long holdTtlSeconds) {
        this.holdTtlSeconds = holdTtlSeconds;
    }

    public boolean tryHold(String eventId, String seatId, String userId) {
        cleanupExpired();
        String key = eventId + ":" + seatId;
        Hold existing = holds.get(key);
        if (existing != null && !existing.isExpired()) {
            return existing.userId().equals(userId);
        }
        holds.put(key, new Hold(userId, Instant.now().plusSeconds(holdTtlSeconds)));
        return true;
    }

    public void release(String eventId, String seatId, String userId) {
        String key = eventId + ":" + seatId;
        holds.computeIfPresent(key, (k, hold) -> hold.userId().equals(userId) ? null : hold);
    }

    public Optional<String> currentHolder(String eventId, String seatId) {
        cleanupExpired();
        Hold hold = holds.get(eventId + ":" + seatId);
        return hold == null || hold.isExpired() ? Optional.empty() : Optional.of(hold.userId());
    }

    private void cleanupExpired() {
        holds.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    private record Hold(String userId, Instant expiresAt) {
        boolean isExpired() {
            return Instant.now().isAfter(expiresAt);
        }
    }
}
