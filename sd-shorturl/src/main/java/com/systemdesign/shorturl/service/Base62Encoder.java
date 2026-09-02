package com.systemdesign.shorturl.service;

public final class Base62Encoder {

    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = 62;

    private Base62Encoder() {
    }

    public static String encode(long number) {
        if (number == 0) {
            return String.valueOf(ALPHABET.charAt(0));
        }
        StringBuilder builder = new StringBuilder();
        long value = number;
        while (value > 0) {
            builder.append(ALPHABET.charAt((int) (value % BASE)));
            value /= BASE;
        }
        return builder.reverse().toString();
    }
}
