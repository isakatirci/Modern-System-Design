package com.systemdesign.blocks;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Prefix trie (prefix tree) — autocomplete ve typeahead senaryoları için kelime indeksi.
 * <p>
 * Her karakter bir {@code Node} üzerinde child map ile temsil edilir; kelimenin son
 * node'unda tam kelime ve {@code frequency} saklanır. Arama, prefix boyunca trie'de
 * yürüyerek eşleşen tüm kelimeleri toplar ve frequency'ye göre sıralar.
 */
public final class PrefixTrie {

    /** Boş prefix'ten başlayan kök node; tüm kelimeler buradan dallanır. */
    private final Node root = new Node();

    /**
     * Trie'ye kelime ekler veya mevcut kelimenin frequency değerini günceller.
     *
     * @param word     indekslenecek kelime (case-insensitive)
     * @param frequency kelimenin popülerlik skoru; yüksek değer öneri listesinde üstte görünür
     * @throws IllegalArgumentException word null veya blank ise
     */
    public void insert(String word, int frequency) {
        if (word == null || word.isBlank()) {
            throw new IllegalArgumentException("word boş olamaz");
        }
        Node current = root;
        // Trie insert: her karakter için child node oluştur veya mevcut olanı takip et
        for (char ch : word.toLowerCase(Locale.ROOT).toCharArray()) {
            current = current.children.computeIfAbsent(ch, key -> new Node());
        }
        current.word = word;
        current.frequency = Math.max(current.frequency, frequency);
    }

    /**
     * Verilen prefix ile başlayan kelimeleri frequency'ye göre azalan sırada döner.
     *
     * @param prefix arama prefix'i (case-insensitive); null ise boş liste
     * @param limit  döndürülecek maksimum sonuç sayısı
     * @return en popüler {@code limit} kadar kelime
     */
    public List<String> suggest(String prefix, int limit) {
        if (prefix == null) {
            return List.of();
        }
        Node current = root;
        // Prefix lookup: prefix boyunca trie'de ilerle; yol yoksa eşleşme yok
        for (char ch : prefix.toLowerCase(Locale.ROOT).toCharArray()) {
            current = current.children.get(ch);
            if (current == null) {
                return List.of();
            }
        }
        List<String> results = new ArrayList<>();
        // Prefix altındaki tüm tam kelimeleri DFS ile topla
        collectAll(current, results);
        results.sort(Comparator.comparingInt(this::frequencyOf).reversed());
        return results.size() > limit ? results.subList(0, limit) : results;
    }

    /** Alt ağaçtaki tüm terminal node'ları (tam kelimeleri) recursive olarak toplar. */
    private void collectAll(Node node, List<String> results) {
        if (node.word != null) {
            results.add(node.word);
        }
        for (Node child : node.children.values()) {
            collectAll(child, results);
        }
    }

    /** Kelimenin trie'deki frequency değerini prefix lookup ile okur. */
    private int frequencyOf(String word) {
        Node current = root;
        for (char ch : word.toLowerCase(Locale.ROOT).toCharArray()) {
            current = current.children.get(ch);
            if (current == null) {
                return 0;
            }
        }
        return current.frequency;
    }

    /** Trie node: karakter → child map, terminal ise word ve frequency tutar. */
    private static final class Node {
        private final Map<Character, Node> children = new HashMap<>();
        private String word;
        private int frequency;
    }
}
