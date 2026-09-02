package com.systemdesign.pastebin.web.dto;

import com.systemdesign.pastebin.domain.PasteVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * Yeni paste oluşturma API request body'si.
 * <p>
 * System design kavramı: <b>API contract / DTO</b> — HTTP katmanından service'e
 * giden veriyi taşır; content validation ve opsiyonel TTL/visibility burada modellenir.
 * <p>
 * {@link com.systemdesign.pastebin.web.PasteController#create} tarafından alınır
 * ve {@link com.systemdesign.pastebin.service.PasteService#create} parametrelerine dönüştürülür.
 *
 * @param content      saklanacak metin (zorunlu)
 * @param ttlSeconds   opsiyonel yaşam süresi saniye cinsinden; null ise süresiz
 * @param visibility   opsiyonel erişim seviyesi; null ise PUBLIC
 */
public record CreatePasteRequest(
        @NotBlank String content,
        @Positive Long ttlSeconds,
        PasteVisibility visibility
) {
}
