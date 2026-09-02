package com.systemdesign.shorturl.service;

/**
 * Sayısal id'leri kısa, URL-safe string'e dönüştüren Base62 encoder utility.
 * <p>
 * System design kavramı: <b>Base62 encoding</b> — 0-9, a-z, A-Z alfabesiyle
 * sayıları compact short key'e çevirir (bit.ly tarzı URL'ler). Hash collision yerine
 * deterministik, sıralı id → string dönüşümü KGS pre-allocation ile birlikte kullanılır.
 */
public final class Base62Encoder {

    // 62 karakter: rakam + küçük harf + büyük harf — URL'de güvenli, case-sensitive
    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = 62;

    private Base62Encoder() {
    }

    /**
     * Verilen pozitif sayıyı Base62 string'e encode eder.
     *
     * @param number encode edilecek sayısal değer
     * @return Base62 short key string'i
     */
    public static String encode(long number) {
        if (number == 0) {
            return String.valueOf(ALPHABET.charAt(0));
        }
        StringBuilder builder = new StringBuilder();
        long value = number;
        // Klasik base conversion: en düşük basamak sondan eklenir, sonra reverse
        while (value > 0) {
            builder.append(ALPHABET.charAt((int) (value % BASE)));
            value /= BASE;
        }
        return builder.reverse().toString();
    }
}
