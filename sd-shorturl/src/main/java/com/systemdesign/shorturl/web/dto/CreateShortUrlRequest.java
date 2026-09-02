package com.systemdesign.shorturl.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Yeni short URL oluşturma API request body'si.
 * <p>
 * System design kavramı: <b>API contract / DTO</b> — HTTP katmanından service katmanına
 * giden veriyi taşır; domain entity'ye dönüşmeden önce validation burada uygulanır.
 * <p>
 * {@link com.systemdesign.shorturl.web.ShortUrlController#create} tarafından alınır
 * ve {@link com.systemdesign.shorturl.service.UrlShortenerService#shorten} için {@code longUrl} sağlar.
 *
 * @param longUrl kısaltılacak orijinal URL (max 2048 karakter)
 */
public record CreateShortUrlRequest(
        @NotBlank @Size(max = 2048) String longUrl
) {
}
