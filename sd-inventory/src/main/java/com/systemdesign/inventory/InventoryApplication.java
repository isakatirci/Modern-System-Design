package com.systemdesign.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Envanter ve koltuk rezervasyon servisinin Spring Boot giriş noktası.
 * <p>
 * Bu modül üç temel sistem tasarımı kavramını bir arada gösterir:
 * <ul>
 *   <li><strong>CAS tabanlı stok</strong> — flash sale'de race condition'sız azaltma</li>
 *   <li><strong>Seat hold + TTL</strong> — bilet satışında geçici kilitleme</li>
 *   <li><strong>Saga pattern</strong> — dağıtık sipariş adımlarında compensation</li>
 * </ul>
 */
@SpringBootApplication
public class InventoryApplication {

    /**
     * Uygulamayı başlatır ve envanter REST endpoint'lerini ayağa kaldırır.
     *
     * @param args komut satırı argümanları
     */
    public static void main(String[] args) {
        SpringApplication.run(InventoryApplication.class, args);
    }
}
