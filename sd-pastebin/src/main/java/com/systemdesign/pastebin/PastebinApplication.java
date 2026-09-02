package com.systemdesign.pastebin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Pastebin servisinin Spring Boot giriş noktası.
 * <p>
 * System design kavramı: <b>Pastebin</b> — kullanıcıların metin snippet'lerini
 * paylaşılabilir linklerle saklayıp okuyabildiği, TTL ve visibility destekli
 * basit key-value benzeri bir depolama servisi.
 * <p>
 * {@code @EnableScheduling} ile süresi dolmuş paste'lerin periyodik temizliği
 * ({@link com.systemdesign.pastebin.service.PasteService#cleanupExpired}) etkinleştirilir.
 * Web, service, repository ve id generation katmanları component scan ile bağlanır.
 */
@SpringBootApplication
@EnableScheduling
public class PastebinApplication {

    /**
     * Uygulamayı başlatır.
     *
     * @param args komut satırı argümanları
     */
    public static void main(String[] args) {
        SpringApplication.run(PastebinApplication.class, args);
    }
}
