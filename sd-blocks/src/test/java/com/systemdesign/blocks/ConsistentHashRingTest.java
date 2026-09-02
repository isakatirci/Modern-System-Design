package com.systemdesign.blocks;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ConsistentHashRingTest {

    @Test
    void distributesKeysAcrossNodes() {
        ConsistentHashRing ring = new ConsistentHashRing(List.of("a", "b", "c"), 100);
        String node1 = ring.getNode("user-1");
        String node2 = ring.getNode("user-2");
        assertEquals(ring.getNode("user-1"), node1);
        assertNotEquals(node1, ring.getNode("user-99999"));
    }
}
