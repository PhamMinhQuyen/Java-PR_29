package com.library.service.impl;

import com.library.domain.model.Book;
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
import com.library.service.LoanService;
import com.library.strategy.FinePolicy;
import com.library.strategy.FinePolicyFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Triển khai nghiệp vụ Mượn và Trả sách.
 * 
 * Áp dụng SOLID:
 * - SRP: Chỉ đảm nhận quy trình mượn/trả sách, không trực tiếp tính toán công thức tiền phạt (giao cho FinePolicy).
 * - OCP: Không hề chứa logic `if-else` hay `switch-case` tính tiền phạt. Nhận FinePolicy thông qua FinePolicyFactory.
 * - LSP: Đối tượng FinePolicy trả về từ Factory có thể thay thế mọi lúc mà không làm hỏng tính đúng đắn.
 * - DIP: Phụ thuộc vào các Repository Interfaces và Factory Abstraction, sử dụng Constructor Injection.
 */
@Service
@Transactional
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final FinePolicyFactory finePolicyFactory;
    private final int defaultBorrowPeriodDays;

    public LoanServiceImpl(LoanRepository loanRepository,
                           BookRepository bookRepository,
                           MemberRepository memberRepository,
                           FinePolicyFactory finePolicyFactory,
                           @Value("${library.fine.borrow-period-days:14}") int defaultBorrowPeriodDays) {
        this.loanRepository = loanRepository;
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
        this.finePolicyFactory = finePolicyFactory;
        this.defaultBorrowPeriodDays = defaultBorrowPeriodDays;
    }

    @Override
    public Loan borrowBook(BorrowBookRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Dữ liệu yêu cầu mượn sách không được null");
        }

        // 1. Kiểm tra sách có tồn tại không
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Sách", request.getBookId()));

        // 2. Kiểm tra sách có khả dụng không
        if (!book.isAvailable()) {
            throw new BookNotAvailableException(book.getId(), book.getTitle());
        }

        // 3. Kiểm tra thành viên có tồn tại và đang hoạt động không
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Thành viên", request.getMemberId()));

        if (!member.isActive()) {
            throw new BusinessException("Tài khoản thành viên '" + member.getName() + "' (ID: " + member.getId() + ") đang bị khóa.");
        }

        // 4. Kiểm tra xem sách có đang trong trạng thái mượn ACTIVE nào khác không
        if (loanRepository.existsByBookIdAndStatus(book.getId(), LoanStatus.ACTIVE)) {
            throw new BookNotAvailableException(book.getId(), book.getTitle());
        }

        // 5. Xác định ngày mượn và ngày hẹn trả
        LocalDate borrowDate = request.getBorrowDate() != null ? request.getBorrowDate() : LocalDate.now();
        LocalDate dueDate = request.getDueDate() != null
                ? request.getDueDate()
                : borrowDate.plusDays(defaultBorrowPeriodDays);

        if (dueDate.isBefore(borrowDate)) {
            throw new BusinessException("Hạn trả sách (" + dueDate + ") không được trước ngày mượn (" + borrowDate + ").");
        }

        // 6. Cập nhật trạng thái sách
        book.markAsBorrowed();
        bookRepository.save(book);

        // 7. Tạo bản ghi mượn mới
        Loan loan = new Loan(book, member, borrowDate, dueDate);
        return loanRepository.save(loan);
    }

    @Override
    public ReturnBookResponse returnBook(Long loanId, ReturnBookRequest request) {
        // 1. Tìm kiếm bản ghi mượn
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Lượt mượn sách", loanId));

        // 2. Kiểm tra trạng thái lượt mượn
        if (!loan.isActive()) {
            throw new InvalidLoanStateException(loanId, "Cuốn sách này đã được trả vào ngày: " + loan.getReturnDate());
        }

        // 3. Xác định ngày trả thực tế
        LocalDate returnDate = (request != null && request.getReturnDate() != null)
                ? request.getReturnDate()
                : LocalDate.now();

        if (returnDate.isBefore(loan.getBorrowDate())) {
            throw new BusinessException("Ngày trả sách (" + returnDate + ") không thể trước ngày mượn (" + loan.getBorrowDate() + ").");
        }

        // 4. Tính số ngày quá hạn
        long overdueDays = loan.calculateOverdueDays(returnDate);

        // 5. Lấy chính sách phạt thông qua Factory (OCP: Hoàn toàn không sửa đổi hàm này khi thêm chính sách mới)
        String requestedPolicy = request != null ? request.getFinePolicy() : null;
        FinePolicy finePolicy = finePolicyFactory.getPolicy(requestedPolicy);

        // 6. Tính phí phạt thông qua Strategy (OCP & LSP)
        BigDecimal fineAmount = finePolicy.calculateFine(overdueDays);

        // 7. Cập nhật lượt mượn và trả lại trạng thái khả dụng cho sách
        loan.markAsReturned(returnDate, fineAmount, finePolicy.getPolicyName());
        bookRepository.save(loan.getBook());
        loanRepository.save(loan);

        // 8. Tạo phản hồi chi tiết
        String message;
        if (overdueDays > 0) {
            message = String.format("Trả sách trễ %d ngày. Áp dụng chính sách phạt '%s'. Số tiền phạt: %s VND.",
                    overdueDays, finePolicy.getPolicyName(), fineAmount.toPlainString());
        } else {
            message = "Trả sách đúng hạn. Không phát sinh phí phạt.";
        }

        return new ReturnBookResponse(
                loan.getId(),
                loan.getBook().getTitle(),
                loan.getMember().getName(),
                loan.getDueDate(),
                loan.getReturnDate(),
                overdueDays,
                fineAmount,
                finePolicy.getPolicyName(),
                message
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Loan getLoanById(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lượt mượn sách", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Loan> getActiveLoans() {
        return loanRepository.findByStatus(LoanStatus.ACTIVE);
    }
}
