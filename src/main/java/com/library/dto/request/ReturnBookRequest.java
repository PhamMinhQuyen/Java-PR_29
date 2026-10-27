package com.library.dto.request;

import java.time.LocalDate;

/**
 * Request payload cho API trả sách.
 * Cho phép chỉ định ngày trả thực tế (để test quá hạn) và chính sách phạt muốn áp dụng (DAILY, FIXED, PROGRESSIVE...).
 */
public class ReturnBookRequest {

    private LocalDate returnDate;
    private String finePolicy;

    public ReturnBookRequest() {
    }

    public ReturnBookRequest(LocalDate returnDate, String finePolicy) {
        this.returnDate = returnDate;
        this.finePolicy = finePolicy;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public String getFinePolicy() {
        return finePolicy;
    }

    public void setFinePolicy(String finePolicy) {
        this.finePolicy = finePolicy;
    }
}
