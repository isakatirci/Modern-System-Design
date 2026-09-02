package com.systemdesign.shorturl.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * KGS key havuzundaki tek bir önceden üretilmiş short key kaydını temsil eden entity.
 * <p>
 * System design kavramı: <b>pre-allocated key pool</b> — short key'ler create anında
 * üretilmez; bu tabloda {@code allocated=false} olarak bekler, kullanıldığında
 * {@code allocated=true} işaretlenir.
 * <p>
 * {@link com.systemdesign.shorturl.service.KeyGenerationService} allocate/preAllocate
 * işlemlerini yürütür; {@link ShortenedUrl} ile eşleşen {@code keyValue} buradan gelir.
 */
@Entity
@Table(name = "short_url_keys")
public class ShortUrlKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 16)
    private String keyValue;

    @Column(nullable = false)
    private boolean allocated;

    protected ShortUrlKey() {
    }

    public ShortUrlKey(String keyValue, boolean allocated) {
        this.keyValue = keyValue;
        this.allocated = allocated;
    }

    public Long getId() {
        return id;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public boolean isAllocated() {
        return allocated;
    }

    public void setAllocated(boolean allocated) {
        this.allocated = allocated;
    }
}
