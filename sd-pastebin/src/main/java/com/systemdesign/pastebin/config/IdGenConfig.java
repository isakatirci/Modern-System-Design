package com.systemdesign.pastebin.config;

import com.systemdesign.pastebin.idgen.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Paste id üretimi için Spring configuration — Snowflake bean tanımı.
 * <p>
 * System design kavramı: <b>distributed unique ID generation</b> — her paste'e
 * global unique, sıralı id vermek için Snowflake algoritması kullanılır;
 * {@code machine-id} ile farklı instance'lar çakışmadan id üretebilir.
 * <p>
 * {@link SnowflakeIdGenerator} bean'i {@link com.systemdesign.pastebin.service.PasteService}
 * tarafından inject edilir; application.properties'ten machine id okunur.
 */
@Configuration
public class IdGenConfig {

    /**
     * Konfigüre edilmiş machine id ile Snowflake id generator bean'i oluşturur.
     *
     * @param machineId bu instance'a atanmış 0–1023 arası machine id
     * @return paylaşılan {@link SnowflakeIdGenerator} instance'ı
     */
    @Bean
    SnowflakeIdGenerator snowflakeIdGenerator(@Value("${systemdesign.snowflake.machine-id}") long machineId) {
        return new SnowflakeIdGenerator(machineId);
    }
}
