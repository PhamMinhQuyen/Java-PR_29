package com.library.service;

import com.library.domain.model.Loan;
import com.library.dto.request.BorrowBookRequest;
import com.library.dto.request.ReturnBookRequest;
import com.library.dto.response.ReturnBookResponse;

import java.util.List;

/**
 * Interface nghiệp vụ Mượn/Trả sách theo nguyên lý ISP.
 * Tách biệt hoàn toàn với quản lý sách hay tài khoản độc giả.
 */
public interface LoanService {

    /**
     * Thực hiện cho mượn sách nếu sách còn khả dụng và thành viên tồn tại.
     */
    Loan borrowBook(BorrowBookRequest request);

    /**
     * Thực hiện trả sách và tính phí phạt theo chính sách được chỉ định.
     */
    ReturnBookResponse returnBook(Long loanId, ReturnBookRequest request);

    /**
     * Lấy thông tin lượt mượn theo ID.
     */
    Loan getLoanById(Long id);

    /**
     * Lấy tất cả lượt mượn.
     */
    List<Loan> getAllLoans();

    /**
     * Lấy danh sách lượt mượn đang active (chưa trả).
     */
    List<Loan> getActiveLoans();
}
