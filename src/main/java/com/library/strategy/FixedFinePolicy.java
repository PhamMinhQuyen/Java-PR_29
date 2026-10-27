package com.library.strategy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Chính sách phạt một mức phí cố định khi quá hạn (Fixed Fine Policy).
 * Công thức: Nếu số ngày trễ > 0 thì phạt một mức cố định, ngược lại = 0.
 * Áp dụng SRP: Chỉ chịu trách nhiệm tính tiền phạt theo mức cố định.
 */
@Component
public class FixedFinePolicy implements FinePolicy {

    public static final String POLICY_NAME = "FIXED";
    private final BigDecimal fixedRate;

    public FixedFinePolicy(@Value("${library.fine.fixed-rate:50000.00}") BigDecimal fixedRate) {
        if (fixedRate == null || fixedRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Mức phạt cố định không được âm.");
        }
        this.fixedRate = fixedRate;
    }

    public FixedFinePolicy() {
        this(BigDecimal.valueOf(50000.00));
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
        return fixedRate;
    }

    public BigDecimal getFixedRate() {
        return fixedRate;
    }
}
