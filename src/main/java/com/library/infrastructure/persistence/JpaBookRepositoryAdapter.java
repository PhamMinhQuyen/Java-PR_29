package com.library.infrastructure.persistence;

import com.library.domain.model.Book;
import com.library.domain.model.BookStatus;
import com.library.domain.repository.BookRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adapter triển khai Domain BookRepository sử dụng Spring Data JPA.
 * Thể hiện nguyên lý DIP: Tầng nghiệp vụ (Domain / Service) chỉ phụ thuộc vào interface BookRepository.
 * Implementation cụ thể này được Spring inject vào lúc runtime.
 */
@Component
public class JpaBookRepositoryAdapter implements BookRepository {

    private final SpringDataBookRepository springDataBookRepository;

    public JpaBookRepositoryAdapter(SpringDataBookRepository springDataBookRepository) {
        this.springDataBookRepository = springDataBookRepository;
    }

    @Override
    public Book save(Book book) {
        return springDataBookRepository.save(book);
    }

    @Override
    public Optional<Book> findById(Long id) {
        return springDataBookRepository.findById(id);
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        return springDataBookRepository.findByIsbn(isbn);
    }

    @Override
    public List<Book> findAll() {
        return springDataBookRepository.findAll();
    }

    @Override
    public List<Book> findByStatus(BookStatus status) {
        return springDataBookRepository.findByStatus(status);
    }

    @Override
    public boolean existsById(Long id) {
        return springDataBookRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        springDataBookRepository.deleteById(id);
    }
}
