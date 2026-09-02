package com.systemdesign.pastebin.web;

import com.systemdesign.pastebin.domain.Paste;
import com.systemdesign.pastebin.service.PasteService;
import com.systemdesign.pastebin.web.dto.CreatePasteRequest;
import com.systemdesign.pastebin.web.dto.PasteResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

/**
 * Pastebin servisinin REST API katmanı.
 * <p>
 * System design kavramı: <b>REST API / Web Layer</b> — paste create ve read
 * endpoint'lerini sunar; request validation ve HTTP status kodlarını yönetir.
 * <p>
 * {@link PasteService} ile iş kurallarını delege eder; domain {@link Paste}
 * entity'sini {@link PasteResponse} DTO'ya dönüştürerek client'a döner.
 */
@RestController
@RequestMapping("/api/v1/pastes")
public class PasteController {

    private final PasteService pasteService;

    public PasteController(PasteService pasteService) {
        this.pasteService = pasteService;
    }

    /**
     * Yeni paste oluşturur; opsiyonel TTL ve visibility ile kaydeder.
     *
     * @param request content, ttlSeconds ve visibility içeren create request
     * @return oluşturulan paste bilgisi (HTTP 201)
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PasteResponse create(@Valid @RequestBody CreatePasteRequest request) {
        Duration ttl = request.ttlSeconds() == null ? null : Duration.ofSeconds(request.ttlSeconds());
        Paste paste = pasteService.create(request.content(), ttl, request.visibility());
        return toResponse(paste);
    }

    /**
     * Id ile paste'i okur; süresi dolmuş kayıtlar bulunamaz sayılır.
     *
     * @param id paste id (Snowflake tabanlı string)
     * @return paste içeriği ve metadata
     * @throws IllegalArgumentException paste yoksa veya expire olduysa
     */
    @GetMapping("/{id}")
    public PasteResponse get(@PathVariable String id) {
        Paste paste = pasteService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paste bulunamadı: " + id));
        return toResponse(paste);
    }

    private PasteResponse toResponse(Paste paste) {
        return new PasteResponse(paste.getId(), paste.getContent(), paste.getExpiresAt(), paste.getVisibility());
    }
}
