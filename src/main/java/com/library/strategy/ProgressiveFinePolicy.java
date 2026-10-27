package com.library.strategy;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Chính sách phạt lũy tiến theo số ngày trễ (Progressive Fine Policy).
 * Minh chứng cho nguyên lý OCP: Thêm chính sách này mà không cần sửa bất kỳ dòng code nào
 * trong LoanService hay các policy có sẵn.
 * - 1 đến 3 ngày: 5.000 VND/ngày
 * - 4 đến 7 ngày: 10.000 VND/ngày cho các ngày từ ngày 4
 * - Từ ngày 8 trở đi: 20.000 VND/ngày cho các ngày từ ngày 8
 */
@Component
public class ProgressiveFinePolicy implements FinePolicy {

    public static final String POLICY_NAME = "PROGRESSIVE";

    private final BigDecimal tier1Rate = BigDecimal.valueOf(5000.00);
    private final BigDecimal tier2Rate = BigDecimal.valueOf(10000.00);
    private final BigDecimal tier3Rate = BigDecimal.valueOf(20000.00);

    @Override
    public String getPolicyName() {
        return POLICY_NAME;
    }

    @Override
    public BigDecimal calculateFine(long overdueDays) {
        if (overdueDays <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalFine = BigDecimal.ZERO;

        if (overdueDays <= 3) {
            totalFine = tier1Rate.multiply(BigDecimal.valueOf(overdueDays));
        } else if (overdueDays <= 7) {
            totalFine = tier1Rate.multiply(BigDecimal.valueOf(3))
                    .add(tier2Rate.multiply(BigDecimal.valueOf(overdueDays - 3)));
        } else {
            totalFine = tier1Rate.multiply(BigDecimal.valueOf(3))
                    .add(tier2Rate.multiply(BigDecimal.valueOf(4)))
                    .add(tier3Rate.multiply(BigDecimal.valueOf(overdueDays - 7)));
        }

        return totalFine;
    }
}
