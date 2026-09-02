package com.systemdesign.blocks;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Prefix trie; autocomplete/typeahead için. */
public final class PrefixTrie {

    private final Node root = new Node();

    public void insert(String word, int frequency) {
        if (word == null || word.isBlank()) {
            throw new IllegalArgumentException("word boş olamaz");
        }
        Node current = root;
        for (char ch : word.toLowerCase(Locale.ROOT).toCharArray()) {
            current = current.children.computeIfAbsent(ch, key -> new Node());
        }
        current.word = word;
        current.frequency = Math.max(current.frequency, frequency);
    }

    public List<String> suggest(String prefix, int limit) {
        if (prefix == null) {
            return List.of();
        }
        Node current = root;
        for (char ch : prefix.toLowerCase(Locale.ROOT).toCharArray()) {
            current = current.children.get(ch);
            if (current == null) {
                return List.of();
            }
        }
        List<String> results = new ArrayList<>();
        collectAll(current, results);
        results.sort(Comparator.comparingInt(this::frequencyOf).reversed());
        return results.size() > limit ? results.subList(0, limit) : results;
    }

    private void collectAll(Node node, List<String> results) {
        if (node.word != null) {
            results.add(node.word);
        }
        for (Node child : node.children.values()) {
            collectAll(child, results);
        }
    }

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

    private static final class Node {
        private final Map<Character, Node> children = new HashMap<>();
        private String word;
        private int frequency;
    }
}
