package com.systemdesign.shorturl.repository;

import com.systemdesign.shorturl.domain.ShortenedUrl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * {@link ShortenedUrl} entity için Spring Data JPA repository — persistence katmanı.
 * <p>
 * System design kavramı: <b>mapping store access</b> — short key → long URL
 * eşlemesinin DB okuma/yazma işlemlerini soyutlar.
 * <p>
 * {@link com.systemdesign.shorturl.service.UrlShortenerService} create ve resolve
 * akışlarında bu repository'yi kullanır; redirect lookup buradan beslenir.
 */
public interface ShortenedUrlRepository extends JpaRepository<ShortenedUrl, Long> {

    /**
     * Short key ile eşleşen URL kaydını arar (redirect resolve için).
     *
     * @param shortKey aranan kısa anahtar
     * @return bulunan entity veya boş
     */
    Optional<ShortenedUrl> findByShortKey(String shortKey);
}
