package com.library.strategy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Unit Tests - Các chính sách tính phí phạt (Fine Policies)")
class FinePolicyTest {

    @Test
    @DisplayName("DailyFinePolicy: Trả đúng hạn hoặc sớm hơn -> Phí phạt = 0")
    void dailyPolicy_onTime_returnsZero() {
        FinePolicy policy = new DailyFinePolicy(BigDecimal.valueOf(5000));
        assertEquals(0, BigDecimal.ZERO.compareTo(policy.calculateFine(0)));
        assertEquals(0, BigDecimal.ZERO.compareTo(policy.calculateFine(-3)));
    }

    @Test
    @DisplayName("DailyFinePolicy: Trả muộn 4 ngày với mức 5,000/ngày -> Phạt 20,000")
    void dailyPolicy_overdue4Days_returnsCalculatedAmount() {
        FinePolicy policy = new DailyFinePolicy(BigDecimal.valueOf(5000));
        BigDecimal fine = policy.calculateFine(4);
        assertEquals(0, BigDecimal.valueOf(20000).compareTo(fine));
    }

    @Test
    @DisplayName("FixedFinePolicy: Trả đúng hạn -> Phí phạt = 0")
    void fixedPolicy_onTime_returnsZero() {
        FinePolicy policy = new FixedFinePolicy(BigDecimal.valueOf(50000));
        assertEquals(0, BigDecimal.ZERO.compareTo(policy.calculateFine(0)));
    }

    @Test
    @DisplayName("FixedFinePolicy: Trả muộn dù 1 ngày hay 10 ngày -> Phạt cố định 50,000")
    void fixedPolicy_overdue_returnsFixedAmount() {
        FinePolicy policy = new FixedFinePolicy(BigDecimal.valueOf(50000));
        assertEquals(0, BigDecimal.valueOf(50000).compareTo(policy.calculateFine(1)));
        assertEquals(0, BigDecimal.valueOf(50000).compareTo(policy.calculateFine(15)));
    }

    @Test
    @DisplayName("ProgressiveFinePolicy: Trả muộn theo từng bậc thang")
    void progressivePolicy_overdue_calculatesTieredRates() {
        FinePolicy policy = new ProgressiveFinePolicy();

        // 0 ngày: 0 VND
        assertEquals(0, BigDecimal.ZERO.compareTo(policy.calculateFine(0)));

        // 2 ngày (bậc 1: 5k/ngày): 2 * 5k = 10k
        assertEquals(0, BigDecimal.valueOf(10000).compareTo(policy.calculateFine(2)));

        // 5 ngày (3 ngày đầu * 5k + 2 ngày sau * 10k): 15k + 20k = 35k
        assertEquals(0, BigDecimal.valueOf(35000).compareTo(policy.calculateFine(5)));

        // 10 ngày (3 ngày đầu * 5k + 4 ngày tiếp * 10k + 3 ngày sau * 20k): 15k + 40k + 60k = 115k
        assertEquals(0, BigDecimal.valueOf(115000).compareTo(policy.calculateFine(10)));
    }

    @Test
    @DisplayName("LSP Test: Mọi FinePolicy đều thay thế được và không trả về số âm")
    void liskovSubstitution_allPoliciesConformToContract() {
        FinePolicy[] policies = {
                new DailyFinePolicy(BigDecimal.valueOf(3000)),
                new FixedFinePolicy(BigDecimal.valueOf(40000)),
                new ProgressiveFinePolicy()
        };

        for (FinePolicy policy : policies) {
            assertNotNull(policy.getPolicyName());
            // Contract LSP: Trả đúng hạn không bao giờ phạt âm hay khác 0
            assertEquals(0, BigDecimal.ZERO.compareTo(policy.calculateFine(0)));
            // Contract LSP: Trả muộn luôn sinh phí phạt >= 0
            assertTrue(policy.calculateFine(5).compareTo(BigDecimal.ZERO) >= 0);
        }
    }
}
