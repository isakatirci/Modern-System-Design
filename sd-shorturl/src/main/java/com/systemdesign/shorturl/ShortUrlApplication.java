package com.systemdesign.shorturl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * URL kısaltma servisinin Spring Boot giriş noktası.
 * <p>
 * System design kavramı: <b>URL Shortener</b> — uzun URL'leri kısa anahtarlara (short key)
 * dönüştürüp HTTP redirect ile orijinal adrese yönlendiren dağıtık sistem bileşeni.
 * <p>
 * Bu sınıf uygulamayı ayağa kaldırır; {@code @EnableCaching} ile redirect lookup
 * sonuçlarının cache katmanında tutulmasını etkinleştirir. Web (controller), service,
 * repository ve domain katmanları Spring component scan ile otomatik bağlanır.
 */
@SpringBootApplication
@EnableCaching
public class ShortUrlApplication {

    /**
     * Uygulamayı başlatır.
     *
     * @param args komut satırı argümanları
     */
    public static void main(String[] args) {
        SpringApplication.run(ShortUrlApplication.class, args);
    }
}
