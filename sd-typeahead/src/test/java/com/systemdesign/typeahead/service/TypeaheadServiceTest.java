package com.systemdesign.typeahead.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class TypeaheadServiceTest {

    @Autowired
    private TypeaheadService typeaheadService;

    @Test
    void returnsSuggestions() {
        typeaheadService.index("system design", 10);
        typeaheadService.index("system architecture", 5);
        List<String> suggestions = typeaheadService.suggest("system", 5);
        assertFalse(suggestions.isEmpty());
    }
}
