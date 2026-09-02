package com.systemdesign.shorturl.repository;

import com.systemdesign.shorturl.domain.ShortUrlKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShortUrlKeyRepository extends JpaRepository<ShortUrlKey, Long> {
    Optional<ShortUrlKey> findFirstByAllocatedFalseOrderByIdAsc();
}
