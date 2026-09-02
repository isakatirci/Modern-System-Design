# System Design Lab — Başlangıç Rehberi

Bu klasör, sistem tasarımı kursundaki (`content.md`) vaka çalışmalarının **çalışan Java kodlarını** içerir.
Her alt klasör **bağımsız bir Maven projesidir** — birbirine bağlı değildir.

**Gereksinimler:** Java 21, Maven 3.9+

---

## Projeler ne anlama geliyor?

| Klasör | Ne öğretir? | Port | Nasıl denerim? |
|--------|-------------|------|----------------|
| `sd-shorturl` | URL kısaltma (TinyURL), cache, KGS | 8081 | `mvn spring-boot:run` → POST `/api/v1/urls` |
| `sd-pastebin` | Metin paylaşım, Snowflake ID, TTL | 8082 | POST `/api/v1/pastes` |
| `sd-feed` | Twitter timeline, push/pull fanout | 8083 | POST `/api/v1/feed/follow` |
| `sd-inventory` | Flash sale stok, koltuk hold, saga | 8084 | POST `/api/v1/inventory/{sku}/init` |
| `sd-typeahead` | Autocomplete, prefix trie | 8085 | GET `/api/v1/typeahead/suggest?prefix=tw` |
| `sd-ratelimit` | API rate limit, token bucket | 8086 | GET `/api/v1/resource` (header: `X-Client-Id`) |
| `sd-common` | Ortak altyapı örnekleri (referans) | — | `mvn test` |
| `sd-idgen` | Snowflake ID generator (referans) | — | `mvn test` |
| `sd-blocks` | Veri yapıları: trie, hash ring (referans) | — | `mvn test` |

---

## Klasör yapısı (her Spring Boot projesi)

```
sd-shorturl/
├── pom.xml                          ← Maven: bağımlılıklar, Java 21, Spring Boot
├── README.md                        ← Proje özeti (varsa)
└── src/
    ├── main/
    │   ├── java/.../               ← Uygulama kodu
    │   │   ├── *Application.java   ← Giriş noktası (main)
    │   │   ├── web/                ← REST controller (HTTP API)
    │   │   ├── service/            ← İş kuralları
    │   │   ├── domain/             ← Entity (DB tablosu karşılığı)
    │   │   └── repository/         ← DB erişimi (Spring Data JPA)
    │   └── resources/
    │       └── application.yml     ← Port, DB, özel ayarlar (yorumlu)
    └── test/                       ← Otomatik testler
```

**Katman akışı:** HTTP request → `Controller` → `Service` → `Repository` → DB

---

## İlk projeyi çalıştırma (sd-shorturl)

```bash
cd projects/sd-shorturl
mvn test
mvn spring-boot:run
```

Başka terminalde (PowerShell):

```powershell
# Kısa URL oluştur
Invoke-RestMethod -Method Post -Uri "http://localhost:8081/api/v1/urls" `
  -ContentType "application/json" `
  -Body '{"longUrl":"https://example.com/uzun-sayfa"}'

# Tarayıcıda dönen shortUrl'e git → orijinal adrese yönlendirir (302 redirect)
```

---

## Config dosyası nerede?

Her boot app'te: `src/main/resources/application.yml`

Dosyanın başında **Türkçe açıklamalar** vardır — port, DB, cache, domain ayarları satır satır anlatılır.

---

## Kod yorumları

- Her Java sınıfında **class Javadoc**: ne yapar, hangi system design kavramını gösterir
- Public method'larda kısa **method Javadoc**
- Algoritma satırlarında **inline comment** (Base62, Snowflake, token bucket, fanout vb.)

Yorum dili: cümleler Türkçe, teknik terimler İngilizce (entity, cache, API, TTL, …).

---

## Tüm projeleri test etme

```powershell
Get-ChildItem c:\Users\isa\SistemTasarim\projects -Directory | ForEach-Object {
  Write-Host "=== $($_.Name) ===" -ForegroundColor Cyan
  Push-Location $_.FullName
  mvn -q test
  Pop-Location
}
```

---

## Referans kütüphaneleri

`sd-common`, `sd-idgen`, `sd-blocks` boot uygulamalarına **Maven dependency olarak bağlı değildir**.
Aynı algoritmaların eğitim amaçlı izole kopyalarıdır; kod ve yorumları okuyarak öğrenebilirsin.

Boot app'lerde gömülü kopyalar:
- `sd-pastebin` → Snowflake ID
- `sd-typeahead` → PrefixTrie
- `sd-ratelimit` → TokenBucketRateLimiter
