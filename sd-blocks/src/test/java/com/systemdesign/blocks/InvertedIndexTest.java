package com.systemdesign.blocks;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InvertedIndexTest {

    @Test
    void findsDocumentsByTerms() {
        InvertedIndex index = new InvertedIndex();
        index.index("d1", "distributed systems design");
        index.index("d2", "system design interview");
        assertEquals(List.of("d1"), index.search("distributed design"));
        assertEquals(List.of("d2"), index.search("interview"));
    }
}
