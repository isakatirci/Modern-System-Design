package com.systemdesign.common;

import org.springframework.stereotype.Service;

@Service
public class CapacityCalculatorService {

    public CapacityProfile estimate(long dau, int writesPerDay, int readsPerDay, int avgBytes) {
        if (dau <= 0 || writesPerDay < 0 || readsPerDay < 0 || avgBytes <= 0) {
            throw new IllegalArgumentException("Kapasite parametreleri geçersiz");
        }
        return new CapacityProfile(dau, writesPerDay, readsPerDay, avgBytes);
    }
}
