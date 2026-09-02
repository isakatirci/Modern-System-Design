package com.systemdesign.blocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/** Basit inverted index; arama motoru building block. */
public final class InvertedIndex {

    private final Map<String, Set<String>> termToDocIds = new HashMap<>();

    public void index(String docId, String text) {
        if (docId == null || docId.isBlank() || text == null) {
            throw new IllegalArgumentException("docId ve text geçerli olmalı");
        }
        for (String term : tokenize(text)) {
            termToDocIds.computeIfAbsent(term, key -> new HashSet<>()).add(docId);
        }
    }

    public List<String> search(String query) {
        List<String> terms = tokenize(query);
        if (terms.isEmpty()) {
            return List.of();
        }
        Set<String> result = new HashSet<>(termToDocIds.getOrDefault(terms.get(0), Set.of()));
        for (int i = 1; i < terms.size(); i++) {
            result.retainAll(termToDocIds.getOrDefault(terms.get(i), Set.of()));
        }
        List<String> sorted = new ArrayList<>(result);
        Collections.sort(sorted);
        return sorted;
    }

    static List<String> tokenize(String text) {
        String[] parts = text.toLowerCase(Locale.ROOT).split("[^a-z0-9]+");
        List<String> tokens = new ArrayList<>();
        for (String part : parts) {
            if (!part.isBlank()) {
                tokens.add(part);
            }
        }
        return tokens;
    }
}
