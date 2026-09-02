package com.systemdesign.shorturl.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * Short key ile long URL arasındaki kalıcı eşlemeyi temsil eden JPA entity.
 * <p>
 * System design kavramı: <b>URL mapping store</b> — redirect sırasında short key
 * ile orijinal URL lookup yapılır; bu tablo sistemin ana veri modelidir.
 * <p>
 * {@link com.systemdesign.shorturl.repository.ShortenedUrlRepository} üzerinden
 * persist edilir; {@link com.systemdesign.shorturl.service.UrlShortenerService} tarafından oluşturulur.
 */
@Entity
@Table(name = "shortened_urls")
public class ShortenedUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 16)
    private String shortKey;

    @Column(nullable = false, length = 2048)
    private String longUrl;

    @Column(nullable = false)
    private Instant createdAt;

    protected ShortenedUrl() {
    }

    public ShortenedUrl(String shortKey, String longUrl, Instant createdAt) {
        this.shortKey = shortKey;
        this.longUrl = longUrl;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getShortKey() {
        return shortKey;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
