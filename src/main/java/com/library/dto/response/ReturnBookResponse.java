package com.library.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ReturnBookResponse {

    private Long loanId;
    private String bookTitle;
    private String memberName;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private long overdueDays;
    private BigDecimal fineAmount;
    private String finePolicyApplied;
    private String statusMessage;

    public ReturnBookResponse() {
    }

    public ReturnBookResponse(Long loanId, String bookTitle, String memberName,
                              LocalDate dueDate, LocalDate returnDate, long overdueDays,
                              BigDecimal fineAmount, String finePolicyApplied, String statusMessage) {
        this.loanId = loanId;
        this.bookTitle = bookTitle;
        this.memberName = memberName;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.overdueDays = overdueDays;
        this.fineAmount = fineAmount;
        this.finePolicyApplied = finePolicyApplied;
        this.statusMessage = statusMessage;
    }

    public Long getLoanId() {
        return loanId;
    }

    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public long getOverdueDays() {
        return overdueDays;
    }

    public void setOverdueDays(long overdueDays) {
        this.overdueDays = overdueDays;
    }

    public BigDecimal getFineAmount() {
        return fineAmount;
    }

    public void setFineAmount(BigDecimal fineAmount) {
        this.fineAmount = fineAmount;
    }

    public String getFinePolicyApplied() {
        return finePolicyApplied;
    }

    public void setFinePolicyApplied(String finePolicyApplied) {
        this.finePolicyApplied = finePolicyApplied;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }
}
