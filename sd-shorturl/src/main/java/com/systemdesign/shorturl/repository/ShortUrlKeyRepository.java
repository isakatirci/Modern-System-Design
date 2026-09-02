package com.systemdesign.shorturl.repository;

import com.systemdesign.shorturl.domain.ShortUrlKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * {@link ShortUrlKey} entity için Spring Data JPA repository — KGS key havuzu erişimi.
 * <p>
 * System design kavramı: <b>key pool persistence</b> — pre-allocated short key'lerin
 * DB'de saklanması ve allocate sırasında atomik tüketimi için kullanılır.
 * <p>
 * {@link com.systemdesign.shorturl.service.KeyGenerationService} allocate ve
 * preAllocate işlemlerinde bu repository üzerinden okur/yazar.
 */
public interface ShortUrlKeyRepository extends JpaRepository<ShortUrlKey, Long> {

    /**
     * Henüz allocate edilmemiş ilk key'i id sırasına göre döner (FIFO tüketim).
     *
     * @return kullanılabilir key veya boş
     */
    Optional<ShortUrlKey> findFirstByAllocatedFalseOrderByIdAsc();
}
