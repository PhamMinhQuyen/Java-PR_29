package com.library.infrastructure.persistence;

import com.library.domain.model.Loan;
import com.library.domain.model.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA Repository cho Loan.
 */
@Repository
public interface SpringDataLoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByStatus(LoanStatus status);

    List<Loan> findByMemberId(Long memberId);

    List<Loan> findByBookId(Long bookId);

    boolean existsByBookIdAndStatus(Long bookId, LoanStatus status);
}
