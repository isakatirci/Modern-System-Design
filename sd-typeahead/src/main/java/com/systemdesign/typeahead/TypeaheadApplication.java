package com.systemdesign.typeahead;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Typeahead (autocomplete) demo uygulamasının Spring Boot entry point'i.
 * <p>
 * Bu servis, kullanıcı yazarken öneri sunmak için {@code PrefixTrie} veri yapısını
 * HTTP API üzerinden expose eder.
 */
@SpringBootApplication
public class TypeaheadApplication {

    /**
     * Uygulamayı başlatır; embedded web server ve Spring context ayağa kalkar.
     *
     * @param args komut satırı argümanları (Spring Boot tarafından parse edilir)
     */
    public static void main(String[] args) {
        SpringApplication.run(TypeaheadApplication.class, args);
    }
}
