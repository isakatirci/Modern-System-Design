# sd-pastebin — Metin Paylaşım

**Kurs:** Ch.26 · **Port:** 8082

## Ne yapar?

Kullanıcı metin yapıştırır, benzersiz bir `id` alır. İsteğe bağlı TTL (süre dolunca silinir).

## System design kavramları

- **Snowflake ID:** Dağıtık ortamda çakışmayan sıralı id
- **TTL cleanup:** `@Scheduled` ile süresi dolmuş paste'leri silme
- **Visibility:** PUBLIC / UNLISTED / PRIVATE enum

## API

| Method | Path | Açıklama |
|--------|------|----------|
| POST | `/api/v1/pastes` | Yeni paste; body: `content`, `ttlSeconds`, `visibility` |
| GET | `/api/v1/pastes/{id}` | Paste oku |

## Config

`application.yml` → `systemdesign.snowflake.machine-id`: Her sunucu instance'ına farklı değer ver.

## Çalıştır

```bash
mvn spring-boot:run
```
