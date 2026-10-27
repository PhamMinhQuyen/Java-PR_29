package com.library.domain.repository;

import com.library.domain.model.Loan;
import com.library.domain.model.LoanStatus;

import java.util.List;
import java.util.Optional;

public interface LoanRepository {

    Loan save(Loan loan);

    Optional<Loan> findById(Long id);

    List<Loan> findAll();

    List<Loan> findByStatus(LoanStatus status);

    List<Loan> findByMemberId(Long memberId);

    List<Loan> findByBookId(Long bookId);

    boolean existsByBookIdAndStatus(Long bookId, LoanStatus status);
}
