package com.systemdesign.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CapacityProfileTest {

    @Test
    void calculatesQpsAndStorage() {
        CapacityProfile profile = new CapacityProfile(1_000_000, 5, 50, 200);
        assertTrue(profile.averageWriteQps() > 0);
        assertTrue(profile.averageReadQps() > profile.averageWriteQps());
        assertEquals(profile.averageReadQps() * 2, profile.peakReadQps(2));
    }
}
