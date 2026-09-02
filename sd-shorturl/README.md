# sd-shorturl — URL Kısaltma (TinyURL)

**Kurs:** Ch.25 · **Port:** 8081

## Ne yapar?

Uzun URL'leri kısa kodlara çevirir. Örnek: `https://example.com/...` → `http://localhost:8081/a1b2c`

## System design kavramları

- **KGS (Key Generation Service):** Short key'ler önceden üretilir (Base62), DB'de `allocated` flag ile dağıtılır
- **Cache-aside:** Redirect okumaları Caffeine cache'ten (hot path)
- **JPA + H2:** Geliştirme DB'si; production'da PostgreSQL kullanılır

## API

| Method | Path | Açıklama |
|--------|------|----------|
| POST | `/api/v1/urls` | Body: `{"longUrl":"..."}` → short URL döner |
| GET | `/{shortKey}` | 302 redirect → orijinal URL |

## Dosya rehberi

| Dosya | Rol |
|-------|-----|
| `ShortUrlApplication.java` | Uygulama girişi |
| `ShortUrlController.java` | HTTP endpoint'ler |
| `UrlShortenerService.java` | Kısaltma + cache resolve |
| `KeyGenerationService.java` | KGS key allocate |
| `Base62Encoder.java` | Sayı → kısa string |
| `application.yml` | Port, H2, cache ayarları |

## Çalıştır

```bash
mvn spring-boot:run
```
