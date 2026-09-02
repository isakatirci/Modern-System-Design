package com.systemdesign.common;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * sd-common modülünün Spring Boot auto-configuration sınıfı.
 * <p>
 * Bu modülü classpath'e ekleyen uygulamalar, {@code CorrelationIdFilter} gibi
 * ortak bean'leri otomatik olarak yükler.
 */
@AutoConfiguration
@ComponentScan(basePackageClasses = CorrelationIdFilter.class)
public class CommonAutoConfiguration {
}
