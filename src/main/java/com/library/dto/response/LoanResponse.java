package com.library.dto.response;

import com.library.domain.model.Loan;
import com.library.domain.model.LoanStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public class LoanResponse {

    private Long id;
    private Long bookId;
    private String bookTitle;
    private String bookIsbn;
    private Long memberId;
    private String memberName;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private BigDecimal fineAmount;
    private String finePolicyApplied;
    private LoanStatus status;

    public LoanResponse() {
    }

    public static LoanResponse fromEntity(Loan loan) {
        if (loan == null) return null;
        LoanResponse res = new LoanResponse();
        res.id = loan.getId();
        if (loan.getBook() != null) {
            res.bookId = loan.getBook().getId();
            res.bookTitle = loan.getBook().getTitle();
            res.bookIsbn = loan.getBook().getIsbn();
        }
        if (loan.getMember() != null) {
            res.memberId = loan.getMember().getId();
            res.memberName = loan.getMember().getName();
        }
        res.borrowDate = loan.getBorrowDate();
        res.dueDate = loan.getDueDate();
        res.returnDate = loan.getReturnDate();
        res.fineAmount = loan.getFineAmount();
        res.finePolicyApplied = loan.getFinePolicyApplied();
        res.status = loan.getStatus();
        return res;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBookIsbn() {
        return bookIsbn;
    }

    public void setBookIsbn(String bookIsbn) {
        this.bookIsbn = bookIsbn;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
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

    public LoanStatus getStatus() {
        return status;
    }

    public void setStatus(LoanStatus status) {
        this.status = status;
    }
}
