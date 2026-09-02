# sd-common — Ortak Altyapı (Referans)

**Kurs:** Ch.1-2 · Boot app'lere bağlı değil; okuyarak öğren.

## Sınıflar

| Sınıf | Ne yapar? |
|-------|-----------|
| `CorrelationIdFilter` | Her HTTP request'e `X-Correlation-Id` ekler (log takibi) |
| `GlobalExceptionHandler` | Hataları RFC 7807 ProblemDetail formatında döner |
| `CapacityProfile` | Back-of-envelope QPS / storage hesabı |
| `SystemReadinessHealthIndicator` | Actuator readiness probe |

```bash
mvn test
```
