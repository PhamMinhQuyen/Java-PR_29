package com.library.service.impl;

import com.library.domain.model.Book;
import com.library.domain.model.BookStatus;
import com.library.domain.repository.BookRepository;
import com.library.dto.request.CreateBookRequest;
import com.library.exception.BusinessException;
import com.library.exception.ResourceNotFoundException;
import com.library.service.BookService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * - SRP: Chỉ đảm nhận logic liên quan tới sách.
 * - DIP: Phụ thuộc vào interface BookRepository (abstraction), dùng constructor injection.
 */
@Service
@Transactional
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book addBook(CreateBookRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Yêu cầu thêm sách không được để null");
        }
        
        bookRepository.findByIsbn(request.getIsbn()).ifPresent(b -> {
            throw new BusinessException("Sách với mã ISBN '" + request.getIsbn() + "' đã tồn tại trong thư viện.");
        });

        Book book = new Book(
                request.getIsbn().trim(),
                request.getTitle().trim(),
                request.getAuthor().trim(),
                request.getCategory() != null ? request.getCategory().trim() : null
        );

        return bookRepository.save(book);
    }

    @Override
    @Transactional(readOnly = true)
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sách", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> getAvailableBooks() {
        return bookRepository.findByStatus(BookStatus.AVAILABLE);
    }
}
