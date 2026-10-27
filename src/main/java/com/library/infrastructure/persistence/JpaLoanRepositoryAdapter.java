package com.library.infrastructure.persistence;

import com.library.domain.model.Loan;
import com.library.domain.model.LoanStatus;
import com.library.domain.repository.LoanRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adapter triển khai Domain LoanRepository sử dụng Spring Data JPA.
 */
@Component
public class JpaLoanRepositoryAdapter implements LoanRepository {

    private final SpringDataLoanRepository springDataLoanRepository;

    public JpaLoanRepositoryAdapter(SpringDataLoanRepository springDataLoanRepository) {
        this.springDataLoanRepository = springDataLoanRepository;
    }

    @Override
    public Loan save(Loan loan) {
        return springDataLoanRepository.save(loan);
    }

    @Override
    public Optional<Loan> findById(Long id) {
        return springDataLoanRepository.findById(id);
    }

    @Override
    public List<Loan> findAll() {
        return springDataLoanRepository.findAll();
    }

    @Override
    public List<Loan> findByStatus(LoanStatus status) {
        return springDataLoanRepository.findByStatus(status);
    }

    @Override
    public List<Loan> findByMemberId(Long memberId) {
        return springDataLoanRepository.findByMemberId(memberId);
    }

    @Override
    public List<Loan> findByBookId(Long bookId) {
        return springDataLoanRepository.findByBookId(bookId);
    }

    @Override
    public boolean existsByBookIdAndStatus(Long bookId, LoanStatus status) {
        return springDataLoanRepository.existsByBookIdAndStatus(bookId, status);
    }
}
