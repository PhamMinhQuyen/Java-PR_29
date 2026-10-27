package com.library.service;

import com.library.domain.model.Book;
import com.library.domain.model.BookStatus;
import com.library.domain.model.Loan;
import com.library.domain.model.LoanStatus;
import com.library.domain.model.Member;
import com.library.domain.repository.BookRepository;
import com.library.domain.repository.LoanRepository;
import com.library.domain.repository.MemberRepository;
import com.library.dto.request.BorrowBookRequest;
import com.library.dto.request.ReturnBookRequest;
import com.library.dto.response.ReturnBookResponse;
import com.library.exception.BookNotAvailableException;
import com.library.exception.BusinessException;
import com.library.exception.InvalidLoanStateException;
import com.library.exception.ResourceNotFoundException;
import com.library.service.impl.LoanServiceImpl;
import com.library.strategy.DailyFinePolicy;
import com.library.strategy.FinePolicyFactory;
import com.library.strategy.FixedFinePolicy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests - LoanService (SOLID: OCP, LSP, DIP, SRP)")
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private MemberRepository memberRepository;

    private FinePolicyFactory finePolicyFactory;
    private LoanService loanService;

    private Book sampleBook;
    private Member sampleMember;

    @BeforeEach
    void setUp() {
        // Khởi tạo FinePolicyFactory với 2 policy chuẩn
        DailyFinePolicy dailyFinePolicy = new DailyFinePolicy(BigDecimal.valueOf(5000));
        FixedFinePolicy fixedFinePolicy = new FixedFinePolicy(BigDecimal.valueOf(50000));
        finePolicyFactory = new FinePolicyFactory(List.of(dailyFinePolicy, fixedFinePolicy), "DAILY");

        loanService = new LoanServiceImpl(
                loanRepository,
                bookRepository,
                memberRepository,
                finePolicyFactory,
                14 // Thời hạn mượn 14 ngày
        );

        sampleBook = new Book(1L, "978-0132350884", "Clean Code", "Robert Martin", "Tech", BookStatus.AVAILABLE);
        sampleMember = new Member(1L, "Nguyễn Văn A", "vana@example.com", "0901234567", true);
    }

    @Test
    @DisplayName("Mượn sách thành công: Sách chuyển BORROWED, hạn trả 14 ngày sau")
    void borrowBook_success() {
        BorrowBookRequest request = new BorrowBookRequest(1L, 1L);
        LocalDate today = LocalDate.now();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(sampleMember));
        when(loanRepository.existsByBookIdAndStatus(1L, LoanStatus.ACTIVE)).thenReturn(false);
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> {
            Loan l = invocation.getArgument(0);
            l.setId(100L);
            return l;
        });

        Loan result = loanService.borrowBook(request);

        assertNotNull(result);
        assertEquals(BookStatus.BORROWED, sampleBook.getStatus());
        assertEquals(today, result.getBorrowDate());
        assertEquals(today.plusDays(14), result.getDueDate());
        assertEquals(LoanStatus.ACTIVE, result.getStatus());

        verify(bookRepository, times(1)).save(sampleBook);
        verify(loanRepository, times(1)).save(any(Loan.class));
    }

    @Test
    @DisplayName("Mượn sách thất bại: Sách đã bị mượn trước đó (BookNotAvailableException)")
    void borrowBook_bookAlreadyBorrowed_throwsException() {
        sampleBook.setStatus(BookStatus.BORROWED);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));

        BorrowBookRequest request = new BorrowBookRequest(1L, 1L);

        assertThrows(BookNotAvailableException.class, () -> loanService.borrowBook(request));
        verify(loanRepository, never()).save(any(Loan.class));
    }

    @Test
    @DisplayName("Mượn sách thất bại: Sách không tồn tại trong hệ thống (ResourceNotFoundException)")
    void borrowBook_bookNotFound_throwsException() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        BorrowBookRequest request = new BorrowBookRequest(99L, 1L);

        assertThrows(ResourceNotFoundException.class, () -> loanService.borrowBook(request));
    }

    @Test
    @DisplayName("Mượn sách thất bại: Tài khoản thành viên bị khóa")
    void borrowBook_memberInactive_throwsException() {
        sampleMember.setActive(false);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));
        when(memberRepository.findById(1L)).thenReturn(Optional.of(sampleMember));

        BorrowBookRequest request = new BorrowBookRequest(1L, 1L);

        assertThrows(BusinessException.class, () -> loanService.borrowBook(request));
    }

    @Test
    @DisplayName("Trả sách đúng hạn: Phí phạt = 0, sách trở về trạng thái AVAILABLE")
    void returnBook_onTime_zeroFine() {
        LocalDate borrowDate = LocalDate.of(2026, 1, 1);
        LocalDate dueDate = LocalDate.of(2026, 1, 15);
        LocalDate actualReturnDate = LocalDate.of(2026, 1, 14); // Trả sớm 1 ngày

        sampleBook.setStatus(BookStatus.BORROWED);
        Loan loan = new Loan(sampleBook, sampleMember, borrowDate, dueDate);
        loan.setId(10L);

        when(loanRepository.findById(10L)).thenReturn(Optional.of(loan));

        ReturnBookRequest request = new ReturnBookRequest(actualReturnDate, "DAILY");
        ReturnBookResponse response = loanService.returnBook(10L, request);

        assertNotNull(response);
        assertEquals(0, response.getOverdueDays());
        assertEquals(0, BigDecimal.ZERO.compareTo(response.getFineAmount()));
        assertEquals(BookStatus.AVAILABLE, sampleBook.getStatus());
        assertEquals(LoanStatus.RETURNED, loan.getStatus());

        verify(bookRepository, times(1)).save(sampleBook);
        verify(loanRepository, times(1)).save(loan);
    }

    @Test
    @DisplayName("Trả sách muộn theo DailyFinePolicy: Quá hạn 5 ngày -> Phạt 25,000 VND")
    void returnBook_overdueWithDailyPolicy() {
        LocalDate borrowDate = LocalDate.of(2026, 1, 1);
        LocalDate dueDate = LocalDate.of(2026, 1, 15);
        LocalDate actualReturnDate = LocalDate.of(2026, 1, 20); // Quá hạn 5 ngày

        sampleBook.setStatus(BookStatus.BORROWED);
        Loan loan = new Loan(sampleBook, sampleMember, borrowDate, dueDate);
        loan.setId(10L);

        when(loanRepository.findById(10L)).thenReturn(Optional.of(loan));

        ReturnBookRequest request = new ReturnBookRequest(actualReturnDate, "DAILY");
        ReturnBookResponse response = loanService.returnBook(10L, request);

        assertEquals(5, response.getOverdueDays());
        assertEquals(0, BigDecimal.valueOf(25000).compareTo(response.getFineAmount()));
        assertEquals("DAILY", response.getFinePolicyApplied());
        assertEquals(BookStatus.AVAILABLE, sampleBook.getStatus());
        assertEquals(LoanStatus.RETURNED, loan.getStatus());
    }

    @Test
    @DisplayName("Trả sách muộn theo FixedFinePolicy: Quá hạn 5 ngày -> Phạt cố định 50,000 VND")
    void returnBook_overdueWithFixedPolicy() {
        LocalDate borrowDate = LocalDate.of(2026, 1, 1);
        LocalDate dueDate = LocalDate.of(2026, 1, 15);
        LocalDate actualReturnDate = LocalDate.of(2026, 1, 20); // Quá hạn 5 ngày

        sampleBook.setStatus(BookStatus.BORROWED);
        Loan loan = new Loan(sampleBook, sampleMember, borrowDate, dueDate);
        loan.setId(10L);

        when(loanRepository.findById(10L)).thenReturn(Optional.of(loan));

        ReturnBookRequest request = new ReturnBookRequest(actualReturnDate, "FIXED");
        ReturnBookResponse response = loanService.returnBook(10L, request);

        assertEquals(5, response.getOverdueDays());
        assertEquals(0, BigDecimal.valueOf(50000).compareTo(response.getFineAmount()));
        assertEquals("FIXED", response.getFinePolicyApplied());
    }

    @Test
    @DisplayName("Trả sách đã hoàn tất trước đó -> ném InvalidLoanStateException")
    void returnBook_alreadyReturned_throwsException() {
        Loan loan = new Loan(sampleBook, sampleMember, LocalDate.now().minusDays(10), LocalDate.now().minusDays(5));
        loan.setId(10L);
        loan.markAsReturned(LocalDate.now().minusDays(2), BigDecimal.ZERO, "DAILY");

        when(loanRepository.findById(10L)).thenReturn(Optional.of(loan));

        ReturnBookRequest request = new ReturnBookRequest(LocalDate.now(), "DAILY");

        assertThrows(InvalidLoanStateException.class, () -> loanService.returnBook(10L, request));
    }
}
