package com.systemdesign.blocks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Basit inverted index — arama motoru building block'u.
 * <p>
 * Her term (kelime) hangi document id'lerde geçtiğini tutar.
 * AND sorgularında tüm term'lerin kesişimi alınır.
 */
public final class InvertedIndex {

    /** term → o term'i içeren document id set'i. */
    private final Map<String, Set<String>> termToDocIds = new HashMap<>();

    /**
     * Bir document'i indeksler; metindeki tüm term'ler docId ile eşleştirilir.
     *
     * @param docId document tanımlayıcısı
     * @param text  indekslenecek ham metin
     * @throws IllegalArgumentException docId veya text geçersiz ise
     */
    public void index(String docId, String text) {
        if (docId == null || docId.isBlank() || text == null) {
            throw new IllegalArgumentException("docId ve text geçerli olmalı");
        }
        for (String term : tokenize(text)) {
            termToDocIds.computeIfAbsent(term, key -> new HashSet<>()).add(docId);
        }
    }

    /**
     * AND sorgusu çalıştırır; tüm term'leri içeren document id'leri döner.
     *
     * @param query arama metni (boşluk veya noktalama ile ayrılmış term'ler)
     * @return eşleşen document id listesi (alfabetik sıralı)
     */
    public List<String> search(String query) {
        List<String> terms = tokenize(query);
        if (terms.isEmpty()) {
            return List.of();
        }
        // İlk term'in doc set'i ile başla
        Set<String> result = new HashSet<>(termToDocIds.getOrDefault(terms.get(0), Set.of()));
        // Kalan term'ler için kesişim (AND) al — hepsini içeren doc'lar kalır
        for (int i = 1; i < terms.size(); i++) {
            result.retainAll(termToDocIds.getOrDefault(terms.get(i), Set.of()));
        }
        List<String> sorted = new ArrayList<>(result);
        Collections.sort(sorted);
        return sorted;
    }

    /**
     * Metni lowercase term listesine böler; alfanumerik olmayan karakterler ayırıcıdır.
     *
     * @param text tokenize edilecek metin
     * @return boş olmayan term listesi
     */
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
