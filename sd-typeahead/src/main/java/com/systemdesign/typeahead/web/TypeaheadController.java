package com.systemdesign.typeahead.web;

import com.systemdesign.typeahead.service.TypeaheadService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/typeahead")
public class TypeaheadController {

    private final TypeaheadService typeaheadService;

    public TypeaheadController(TypeaheadService typeaheadService) {
        this.typeaheadService = typeaheadService;
    }

    @PostMapping("/index")
    public Map<String, String> index(@RequestParam String phrase, @RequestParam(defaultValue = "1") int frequency) {
        typeaheadService.index(phrase, frequency);
        return Map.of("status", "indexed");
    }

    @GetMapping("/suggest")
    public List<String> suggest(@RequestParam String prefix, @RequestParam(defaultValue = "5") int limit) {
        return typeaheadService.suggest(prefix, limit);
    }
}
