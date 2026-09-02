# sd-ratelimit — API Rate Limiter

**Kurs:** Ch.17 / Ch.33 · **Port:** 8086

## Ne yapar?

Her `clientId` için istek hızını sınırlar. Limit aşılınca **HTTP 429** döner.

## System design kavramları

- **Token bucket:** Kovada token birikir; istek token harcar; burst'e izin verir
- **Per-client limiter:** `X-Client-Id` header ile ayrı bucket

## Config (`application.yml`)

- `capacity: 10` — anlık burst (max token)
- `refill-per-second: 5` — saniyede dolan token

## API

```http
GET /api/v1/resource
X-Client-Id: user-123
```

200 OK veya 429 Too Many Requests

## Çalıştır

```bash
mvn spring-boot:run
```
