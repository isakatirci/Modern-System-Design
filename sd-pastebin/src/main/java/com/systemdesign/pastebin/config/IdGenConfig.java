package com.systemdesign.pastebin.config;

import com.systemdesign.pastebin.idgen.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdGenConfig {

    @Bean
    SnowflakeIdGenerator snowflakeIdGenerator(@Value("${systemdesign.snowflake.machine-id}") long machineId) {
        return new SnowflakeIdGenerator(machineId);
    }
}
