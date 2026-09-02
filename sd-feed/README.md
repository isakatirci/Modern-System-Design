# sd-feed — Twitter Timeline (Hybrid Fanout)

**Kurs:** Ch.30 · **Port:** 8083

## Ne yapar?

Kullanıcılar birbirini takip eder; post atılınca timeline güncellenir.

## System design kavramları

- **Push fanout:** Az takipçili yazarlarda post, takipçi timeline'larına yazılır (hızlı okuma)
- **Pull fanout:** Celebrity (çok takipçili) yazarlarda okuma anında birleştirilir (yazma maliyeti düşük)
- Eşik: `push-follower-threshold` (varsayılan 10.000)

## API

| Method | Path | Parametreler |
|--------|------|--------------|
| POST | `/api/v1/feed/follow` | `followerId`, `authorId` |
| POST | `/api/v1/feed/posts` | `authorId`, `postId` |
| GET | `/api/v1/feed/{userId}` | Kullanıcının timeline listesi |

## Çalıştır

```bash
mvn spring-boot:run
```
