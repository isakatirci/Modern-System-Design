package com.systemdesign.shorturl.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Base62EncoderTest {

    @Test
    void encodesNumbers() {
        assertEquals("1", Base62Encoder.encode(1));
        assertEquals("10", Base62Encoder.encode(62));
    }
}
