package com.library.infrastructure.persistence;

import com.library.domain.model.Book;
import com.library.domain.model.BookStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository cho Book.
 * Đây là Low-Level component trong DIP.
 */
@Repository
public interface SpringDataBookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    List<Book> findByStatus(BookStatus status);
}
