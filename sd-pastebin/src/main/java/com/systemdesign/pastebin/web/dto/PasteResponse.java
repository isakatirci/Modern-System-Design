package com.systemdesign.pastebin.web.dto;

import com.systemdesign.pastebin.domain.PasteVisibility;

import java.time.Instant;

public record PasteResponse(String id, String content, Instant expiresAt, PasteVisibility visibility) {
}
