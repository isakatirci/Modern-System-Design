package com.systemdesign.pastebin.web.dto;

import com.systemdesign.pastebin.domain.PasteVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CreatePasteRequest(
        @NotBlank String content,
        @Positive Long ttlSeconds,
        PasteVisibility visibility
) {
}
