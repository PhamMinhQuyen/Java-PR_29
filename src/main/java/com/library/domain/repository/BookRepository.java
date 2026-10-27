package com.library.domain.repository;

import com.library.domain.model.Book;
import com.library.domain.model.BookStatus;

import java.util.List;
import java.util.Optional;

public interface BookRepository {

    Book save(Book book);

    Optional<Book> findById(Long id);

    Optional<Book> findByIsbn(String isbn);

    List<Book> findAll();

    List<Book> findByStatus(BookStatus status);

    boolean existsById(Long id);

    void deleteById(Long id);
}
