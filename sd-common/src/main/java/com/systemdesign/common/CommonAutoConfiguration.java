package com.systemdesign.common;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ComponentScan(basePackageClasses = CorrelationIdFilter.class)
public class CommonAutoConfiguration {
}
