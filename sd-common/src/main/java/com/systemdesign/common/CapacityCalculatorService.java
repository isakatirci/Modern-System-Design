package com.systemdesign.common;

import org.springframework.stereotype.Service;

/**
 * Back-of-the-envelope kapasite hesaplama servisi.
 * <p>
 * DAU, read/write oranları ve ortalama object boyutundan QPS ve storage
 * tahminleri üreten {@code CapacityProfile} oluşturur.
 */
@Service
public class CapacityCalculatorService {

    /**
     * Verilen parametrelerle kapasite profili hesaplar.
     *
     * @param dau           günlük aktif kullanıcı sayısı (Daily Active Users)
     * @param writesPerDay  kullanıcı başına günlük write sayısı
     * @param readsPerDay   kullanıcı başına günlük read sayısı
     * @param avgBytes      ortalama object boyutu (byte)
     * @return hesaplanmış kapasite metriklerini içeren profil
     * @throws IllegalArgumentException parametreler geçersiz veya negatif ise
     */
    public CapacityProfile estimate(long dau, int writesPerDay, int readsPerDay, int avgBytes) {
        if (dau <= 0 || writesPerDay < 0 || readsPerDay < 0 || avgBytes <= 0) {
            throw new IllegalArgumentException("Kapasite parametreleri geçersiz");
        }
        return new CapacityProfile(dau, writesPerDay, readsPerDay, avgBytes);
    }
}
