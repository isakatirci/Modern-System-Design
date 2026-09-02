package com.systemdesign.pastebin.repository;

import com.systemdesign.pastebin.domain.Paste;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

/**
 * {@link Paste} entity için Spring Data JPA repository — persistence katmanı.
 * <p>
 * System design kavramı: <b>content store access + TTL cleanup</b> — paste CRUD
 * işlemlerini soyutlar; expire olmuş kayıtların toplu silinmesi için özel query sunar.
 * <p>
 * {@link com.systemdesign.pastebin.service.PasteService} create/find ve scheduled
 * {@code cleanupExpired} akışlarında bu repository kullanılır.
 */
public interface PasteRepository extends JpaRepository<Paste, String> {

    /**
     * {@code expiresAt} geçmiş tüm paste kayıtlarını siler (TTL garbage collection).
     *
     * @param now karşılaştırma anı
     * @return silinen kayıt sayısı
     */
    @Modifying
    @Query("delete from Paste p where p.expiresAt is not null and p.expiresAt < :now")
    int deleteExpired(@Param("now") Instant now);
}
