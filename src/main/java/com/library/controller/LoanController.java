package com.library.controller;

import com.library.domain.model.Loan;
import com.library.dto.request.BorrowBookRequest;
import com.library.dto.request.ReturnBookRequest;
import com.library.dto.response.ApiResponse;
import com.library.dto.response.LoanResponse;
import com.library.dto.response.ReturnBookResponse;
import com.library.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    /**
     * API 1: Cho phép thành viên mượn sách.
     * POST /api/loans
     */
    @PostMapping
    public ResponseEntity<ApiResponse<LoanResponse>> borrowBook(@Valid @RequestBody BorrowBookRequest request) {
        Loan loan = loanService.borrowBook(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(LoanResponse.fromEntity(loan), "Mượn sách thành công"));
    }

    /**
     * API 2: Cho phép thành viên trả sách và tính phí phạt.
     * POST /api/loans/{id}/return
     */
    @PostMapping("/{id}/return")
    public ResponseEntity<ApiResponse<ReturnBookResponse>> returnBook(
            @PathVariable Long id,
            @RequestBody(required = false) ReturnBookRequest request) {
        ReturnBookResponse response = loanService.returnBook(id, request);
        return ResponseEntity.ok(ApiResponse.ok(response, "Thao tác trả sách thành công"));
    }

    /**
     * API 3: Lấy danh sách toàn bộ các lượt mượn sách.
     * GET /api/loans
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<LoanResponse>>> getAllLoans() {
        List<LoanResponse> list = loanService.getAllLoans().stream()
                .map(LoanResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.ok(list, "Lấy danh sách các lượt mượn thành công"));
    }

    /**
     * API 4: Lấy danh sách các cuốn sách đang được mượn (chưa trả).
     * GET /api/loans/active
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<LoanResponse>>> getActiveLoans() {
        List<LoanResponse> list = loanService.getActiveLoans().stream()
                .map(LoanResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.ok(list, "Lấy danh sách lượt mượn đang active thành công"));
    }

    /**
     * API 5: Lấy thông tin chi tiết của một lượt mượn.
     * GET /api/loans/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LoanResponse>> getLoanById(@PathVariable Long id) {
        Loan loan = loanService.getLoanById(id);
        return ResponseEntity.ok(ApiResponse.ok(LoanResponse.fromEntity(loan), "Lấy thông tin lượt mượn thành công"));
    }
}
