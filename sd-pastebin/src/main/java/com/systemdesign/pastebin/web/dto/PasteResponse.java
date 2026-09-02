package com.systemdesign.pastebin.web.dto;

import com.systemdesign.pastebin.domain.PasteVisibility;

import java.time.Instant;

/**
 * Paste okuma/oluşturma API response'u.
 * <p>
 * System design kavramı: <b>API response DTO</b> — domain {@link com.systemdesign.pastebin.domain.Paste}
 * entity'sini HTTP client'a uygun formatta döner; internal DB alanları gizlenir.
 * <p>
 * {@link com.systemdesign.pastebin.web.PasteController} create ve get endpoint'lerinden döner.
 *
 * @param id         paste id (Snowflake tabanlı string)
 * @param content    saklanan metin
 * @param expiresAt  TTL bitiş zamanı; null ise süresiz
 * @param visibility erişim seviyesi
 */
public record PasteResponse(String id, String content, Instant expiresAt, PasteVisibility visibility) {
}
