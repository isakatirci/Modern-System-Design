package com.systemdesign.inventory.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SeatReservationServiceTest {

    @Test
    void holdsSeatExclusively() {
        SeatReservationService service = new SeatReservationService(900);
        assertTrue(service.tryHold("e1", "A1", "user-1"));
        assertFalse(service.tryHold("e1", "A1", "user-2"));
        assertTrue(service.tryHold("e1", "A1", "user-1"));
    }
}
