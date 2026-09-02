package com.systemdesign.shorturl.repository;

import com.systemdesign.shorturl.domain.ShortenedUrl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShortenedUrlRepository extends JpaRepository<ShortenedUrl, Long> {
    Optional<ShortenedUrl> findByShortKey(String shortKey);
}
