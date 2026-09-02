package com.systemdesign.pastebin.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * Tek bir paste kaydını temsil eden JPA entity.
 * <p>
 * System design kavramı: <b>content store with TTL</b> — metin içeriği Snowflake id
 * ile adreslenir; {@code expiresAt} ile otomatik silme (TTL) desteklenir;
 * {@link PasteVisibility} ile erişim seviyesi modellenir.
 * <p>
 * {@link com.systemdesign.pastebin.repository.PasteRepository} üzerinden persist edilir;
 * {@link com.systemdesign.pastebin.service.PasteService} create/read/cleanup akışlarında kullanılır.
 */
@Entity
@Table(name = "pastes")
public class Paste {

    @Id
    @Column(length = 32)
    private String id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column
    private Instant expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private PasteVisibility visibility;

    @Column(nullable = false)
    private Instant createdAt;

    protected Paste() {
    }

    public Paste(String id, String content, Instant expiresAt, PasteVisibility visibility, Instant createdAt) {
        this.id = id;
        this.content = content;
        this.expiresAt = expiresAt;
        this.visibility = visibility;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public PasteVisibility getVisibility() {
        return visibility;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * Verilen anda paste'in süresinin dolup dolmadığını kontrol eder.
     *
     * @param now karşılaştırma anı
     * @return TTL yoksa false; expiresAt geçmişse true
     */
    public boolean isExpired(Instant now) {
        return expiresAt != null && now.isAfter(expiresAt);
    }
}
