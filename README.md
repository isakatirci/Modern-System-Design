# System Design Lab — Bağımsız Maven Projeleri

Kurs (`content.md`) vaka analizleri ve building block'ları için **birbirinden bağımsız** Maven projeleri.
Parent POM yok; her klasör kendi başına build edilir.

Java 21 · Spring Boot 3.4.2

## Projeler

| Proje | Tür | Kurs | Port |
|-------|-----|------|------|
| `sd-common` | library (referans) | Ch.1-2 ortak altyapı | — |
| `sd-idgen` | library (referans) | Ch.19 Snowflake ID | — |
| `sd-blocks` | library (referans) | Ch.12,17,20,32 building blocks | — |
| `sd-shorturl` | boot app | Ch.25 TinyURL | 8081 |
| `sd-pastebin` | boot app | Ch.26 Pastebin | 8082 |
| `sd-feed` | boot app | Ch.30 Twitter hybrid fanout | 8083 |
| `sd-inventory` | boot app | Ch.39 Ticketmaster + Ch.44 Flash Sale | 8084 |
| `sd-typeahead` | boot app | Ch.32 Autocomplete | 8085 |
| `sd-ratelimit` | boot app | Ch.17/33 Rate Limiter | 8086 |

Boot uygulamaları arasında Maven dependency yok. Gerekli kod (Snowflake, PrefixTrie, TokenBucket) ilgili projeye gömülüdür.

## Build & Test

Tek proje:

```bash
cd projects/sd-shorturl
mvn test
```

Tüm projeler (PowerShell):

```powershell
Get-ChildItem projects -Directory | ForEach-Object {
  Push-Location $_.FullName
  mvn -q test
  Pop-Location
}
```

Çalıştırma:

```bash
cd projects/sd-shorturl
mvn spring-boot:run
```

## Mimari

- **sd-shorturl**: KGS + Base62, JPA, Caffeine cache-aside, redirect API
- **sd-pastebin**: gömülü Snowflake ID, TTL cleanup, visibility
- **sd-feed**: push/pull hybrid timeline (celebrity threshold)
- **sd-inventory**: atomic stock decrement, seat hold TTL, saga orchestrator
- **sd-typeahead**: gömülü prefix trie suggest
- **sd-ratelimit**: gömülü token bucket per client, HTTP 429
- **sd-common / sd-idgen / sd-blocks**: referans kütüphaneleri; boot app'ler bunlara bağlı değil
