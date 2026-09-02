package com.systemdesign.shorturl.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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
