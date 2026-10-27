package com.library.service;

import com.library.domain.model.Book;
import com.library.domain.model.BookStatus;
import com.library.domain.model.Loan;
import com.library.domain.model.Member;
import com.library.domain.repository.BookRepository;
import com.library.domain.repository.LoanRepository;
import com.library.domain.repository.MemberRepository;
import com.library.dto.request.ReturnBookRequest;
import com.library.dto.response.ReturnBookResponse;
import com.library.service.impl.LoanServiceImpl;
import com.library.strategy.DailyFinePolicy;
import com.library.strategy.FinePolicy;
import com.library.strategy.FinePolicyFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * Test case đặc biệt chứng minh nguyên lý OCP (Open/Closed Principle):
 * Có thể thêm một chính sách phạt hoàn toàn mới (ví dụ: Sinh viên giảm 50% phí phạt)
 * mà KHÔNG cần sửa bất kỳ dòng code nào trong LoanService hay các policy cũ.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests - Chứng minh nguyên lý OCP (Open/Closed Principle)")
class OpenClosedPrincipleTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private MemberRepository memberRepository;

    /**
     * 1. Định nghĩa một chính sách phạt hoàn toàn mới: STUDENT_POLICY
     * Phạt 2.000 VND / ngày trễ (ưu đãi sinh viên)
     */
    static class StudentFinePolicy implements FinePolicy {
        @Override
        public String getPolicyName() {
            return "STUDENT";
        }

        @Override
        public BigDecimal calculateFine(long overdueDays) {
            if (overdueDays <= 0) {
                return BigDecimal.ZERO;
            }
            return BigDecimal.valueOf(2000).multiply(BigDecimal.valueOf(overdueDays));
        }
    }

    @Test
    @DisplayName("OCP: Thêm StudentFinePolicy mới mà LoanService vẫn hoạt động chính xác không cần sửa code")
    void testOpenClosedPrinciple_newPolicyAddedWithoutModifyingLoanService() {
        // Danh sách policy ban đầu
        List<FinePolicy> existingPolicies = new ArrayList<>();
        existingPolicies.add(new DailyFinePolicy());

        // Bổ sung policy mới vào danh sách (trong môi trường Spring, chỉ cần thêm class @Component)
        StudentFinePolicy studentPolicy = new StudentFinePolicy();
        existingPolicies.add(studentPolicy);

        FinePolicyFactory factory = new FinePolicyFactory(existingPolicies, "DAILY");

        // LoanService được khởi tạo bình thường mà KHÔNG biết gì về StudentFinePolicy
        LoanService loanService = new LoanServiceImpl(loanRepository, bookRepository, memberRepository, factory, 14);

        Book book = new Book(1L, "978-0132350884", "Clean Code", "Uncle Bob", "IT", BookStatus.BORROWED);
        Member member = new Member(1L, "Sinh Viên X", "sv@university.edu.vn", "0900000000", true);
        Loan loan = new Loan(book, member, LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 15));
        loan.setId(50L);

        when(loanRepository.findById(50L)).thenReturn(Optional.of(loan));

        // Trả sách trễ 6 ngày (ngày 21/03) với chính sách STUDENT mới toanh
        ReturnBookRequest request = new ReturnBookRequest(LocalDate.of(2026, 3, 21), "STUDENT");
        ReturnBookResponse response = loanService.returnBook(50L, request);

        // 6 ngày * 2.000 = 12.000 VND
        assertNotNull(response);
        assertEquals(6, response.getOverdueDays());
        assertEquals(0, BigDecimal.valueOf(12000).compareTo(response.getFineAmount()));
        assertEquals("STUDENT", response.getFinePolicyApplied());
    }
}
