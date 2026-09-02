# sd-inventory — Stok ve Bilet Hold

**Kurs:** Ch.39 (Ticketmaster) + Ch.44 (Flash Sale) · **Port:** 8084

## Ne yapar?

1. **Flash sale:** Sınırlı stoktan eşzamanlı satın alma (race condition önleme)
2. **Ticketing:** Koltuk geçici hold — ödeme süresi dolunca serbest kalır

## System design kavramları

- **CAS (Compare-And-Set):** `AtomicInteger` ile lock-free stok azaltma
- **Seat hold + TTL:** `hold-ttl-seconds` (900 = 15 dk)
- **Saga:** `OrderSagaOrchestrator` — ödeme fail olursa stok geri verilir (demo)

## API

| Method | Path | Açıklama |
|--------|------|----------|
| POST | `/api/v1/inventory/{sku}/init?quantity=100` | Stok tanımla |
| POST | `/api/v1/inventory/{sku}/purchase` | Stok dene |
| POST | `/api/v1/events/{eventId}/seats/{seatId}/hold?userId=u1` | Koltuk tut |
| GET | `/api/v1/events/{eventId}/seats/{seatId}/holder` | Hold sahibi |

## Çalıştır

```bash
mvn spring-boot:run
```
