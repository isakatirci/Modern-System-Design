# sd-typeahead — Autocomplete

**Kurs:** Ch.32 · **Port:** 8085

## Ne yapar?

Arama kutusuna yazılan prefix'e göre öneri listesi döner (Google arama önerisi gibi).

## System design kavramları

- **Prefix trie:** Her karakter bir node; prefix ile hızlı arama
- **Frequency ranking:** Aynı prefix'te daha popüler kelime önce gelir

## API

| Method | Path | Örnek |
|--------|------|-------|
| POST | `/api/v1/typeahead/index?phrase=twitter&frequency=10` | Kelime ekle |
| GET | `/api/v1/typeahead/suggest?prefix=tw&limit=5` | Öneri al |

## Çalıştır

```bash
mvn spring-boot:run
```
