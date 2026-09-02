package com.systemdesign.feed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Sosyal feed (timeline) servisinin Spring Boot giriş noktası.
 * <p>
 * Bu modül, sistem tasarımındaki <strong>hybrid fanout</strong> pattern'ini gösterir:
 * düşük follower sayısında post'lar takipçilere <em>push</em> edilir,
 * celebrity hesaplarda ise okuma anında <em>pull</em> ile birleştirilir.
 */
@SpringBootApplication
public class FeedApplication {

    /**
     * Uygulamayı başlatır ve REST endpoint'lerini ayağa kaldırır.
     *
     * @param args komut satırı argümanları
     */
    public static void main(String[] args) {
        SpringApplication.run(FeedApplication.class, args);
    }
}
