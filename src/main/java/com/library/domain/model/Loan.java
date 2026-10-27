package com.library.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "borrow_date", nullable = false)
    private LocalDate borrowDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "return_date")
    private LocalDate returnDate;

    @Column(name = "fine_amount", precision = 12, scale = 2)
    private BigDecimal fineAmount;

    @Column(name = "fine_policy_applied", length = 50)
    private String finePolicyApplied;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LoanStatus status;

    public Loan() {
        this.status = LoanStatus.ACTIVE;
        this.fineAmount = BigDecimal.ZERO;
    }

    public Loan(Book book, Member member, LocalDate borrowDate, LocalDate dueDate) {
        if (dueDate.isBefore(borrowDate)) {
            throw new IllegalArgumentException("Hạn trả sách không thể trước ngày mượn.");
        }
        this.book = book;
        this.member = member;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.status = LoanStatus.ACTIVE;
        this.fineAmount = BigDecimal.ZERO;
    }

    /**
     * Tính số ngày trễ hạn dựa trên ngày trả thực tế.
     *
     * @param actualReturnDate Ngày độc giả trả sách
     * @return Số ngày quá hạn (0 nếu trả đúng hạn hoặc sớm hơn)
     */
    public long calculateOverdueDays(LocalDate actualReturnDate) {
        if (actualReturnDate == null || !actualReturnDate.isAfter(this.dueDate)) {
            return 0;
        }
        return ChronoUnit.DAYS.between(this.dueDate, actualReturnDate);
    }

    /**
     * Hoàn tất trả sách, cập nhật thông tin phí phạt và đổi trạng thái.
     */
    public void markAsReturned(LocalDate actualReturnDate, BigDecimal calculatedFine, String policyName) {
        if (this.status == LoanStatus.RETURNED) {
            throw new IllegalStateException("Lượt mượn này đã được trả trước đó.");
        }
        this.returnDate = actualReturnDate;
        this.fineAmount = calculatedFine != null ? calculatedFine : BigDecimal.ZERO;
        this.finePolicyApplied = policyName;
        this.status = LoanStatus.RETURNED;
        
        // Trả lại trạng thái sách sẵn sàng
        this.book.markAsAvailable();
    }

    public boolean isActive() {
        return LoanStatus.ACTIVE.equals(this.status);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
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
