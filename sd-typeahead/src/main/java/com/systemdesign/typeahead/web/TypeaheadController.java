package com.systemdesign.typeahead.web;

import com.systemdesign.typeahead.service.TypeaheadService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Typeahead REST API controller'ı.
 * <p>
 * {@code /index} endpoint'i kelime ekler; {@code /suggest} endpoint'i
 * autocomplete önerilerini döner.
 */
@RestController
@RequestMapping("/api/v1/typeahead")
public class TypeaheadController {

    private final TypeaheadService typeaheadService;

    /**
     * Spring dependency injection ile service bean'ini alır.
     *
     * @param typeaheadService typeahead iş mantığı servisi
     */
    public TypeaheadController(TypeaheadService typeaheadService) {
        this.typeaheadService = typeaheadService;
    }

    /**
     * Kelime veya cümleyi typeahead indeksine ekler.
     *
     * @param phrase    indekslenecek metin
     * @param frequency popülerlik skoru (varsayılan 1)
     * @return işlem durumu
     */
    @PostMapping("/index")
    public Map<String, String> index(@RequestParam String phrase, @RequestParam(defaultValue = "1") int frequency) {
        typeaheadService.index(phrase, frequency);
        return Map.of("status", "indexed");
    }

    /**
     * Prefix'e göre autocomplete önerilerini döner.
     *
     * @param prefix arama prefix'i
     * @param limit  maksimum sonuç sayısı (varsayılan 5)
     * @return popülerlik sırasına göre kelime listesi
     */
    @GetMapping("/suggest")
    public List<String> suggest(@RequestParam String prefix, @RequestParam(defaultValue = "5") int limit) {
        return typeaheadService.suggest(prefix, limit);
    }
}
