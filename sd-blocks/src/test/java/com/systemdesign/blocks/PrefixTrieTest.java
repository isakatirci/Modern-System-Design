package com.systemdesign.blocks;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PrefixTrieTest {

    @Test
    void suggestsByPrefixAndFrequency() {
        PrefixTrie trie = new PrefixTrie();
        trie.insert("twitter", 10);
        trie.insert("twitch", 5);
        trie.insert("twilio", 8);
        assertEquals(List.of("twitter"), trie.suggest("twi", 1));
        assertTrue(trie.suggest("tw", 3).contains("twitter"));
    }
}
