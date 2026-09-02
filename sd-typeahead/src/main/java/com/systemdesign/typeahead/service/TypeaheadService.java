package com.systemdesign.typeahead.service;

import com.systemdesign.typeahead.trie.PrefixTrie;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TypeaheadService {

    private final PrefixTrie trie = new PrefixTrie();

    public void index(String phrase, int frequency) {
        trie.insert(phrase, frequency);
    }

    public List<String> suggest(String prefix, int limit) {
        return trie.suggest(prefix, limit);
    }
}
