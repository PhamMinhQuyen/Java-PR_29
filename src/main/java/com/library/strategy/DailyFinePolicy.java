package com.library.strategy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Chính sách phạt theo từng ngày quá hạn (Daily Fine Policy).
 * Công thức: Số tiền phạt = Số ngày trễ * Mức phạt mỗi ngày.
 * Áp dụng SRP: Chỉ chịu trách nhiệm tính tiền phạt theo ngày trễ.
 */
@Component
public class DailyFinePolicy implements FinePolicy {

    public static final String POLICY_NAME = "DAILY";
    private final BigDecimal dailyRate;

    public DailyFinePolicy(@Value("${library.fine.daily-rate:5000.00}") BigDecimal dailyRate) {
        if (dailyRate == null || dailyRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Mức phạt theo ngày không được âm.");
        }
        this.dailyRate = dailyRate;
    }

    public DailyFinePolicy() {
        this(BigDecimal.valueOf(5000.00));
    }

    @Override
    public String getPolicyName() {
        return POLICY_NAME;
    }

    @Override
    public BigDecimal calculateFine(long overdueDays) {
        if (overdueDays <= 0) {
            return BigDecimal.ZERO;
        }
        return dailyRate.multiply(BigDecimal.valueOf(overdueDays));
    }

    public BigDecimal getDailyRate() {
        return dailyRate;
    }
}
