package com.systemdesign.shorturl.web.dto;

/**
 * Short URL oluşturma API response'u.
 * <p>
 * System design kavramı: <b>API response DTO</b> — domain {@link com.systemdesign.shorturl.domain.ShortenedUrl}
 * entity'sini HTTP client'a uygun, serialization-friendly formatta döner.
 * <p>
 * {@link com.systemdesign.shorturl.web.ShortUrlController#create} tarafından üretilir;
 * client short key, tam short URL ve orijinal long URL bilgisini alır.
 *
 * @param shortKey   kısa anahtar (path segment)
 * @param shortUrl   tam kısa URL (base host + short key)
 * @param longUrl    orijinal uzun URL
 */
public record ShortUrlResponse(String shortKey, String shortUrl, String longUrl) {
}
