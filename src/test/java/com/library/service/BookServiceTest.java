package com.library.service;

import com.library.domain.model.Book;
import com.library.domain.model.BookStatus;
import com.library.domain.repository.BookRepository;
import com.library.dto.request.CreateBookRequest;
import com.library.exception.BusinessException;
import com.library.exception.ResourceNotFoundException;
import com.library.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests - BookService (SRP & DIP)")
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    private BookService bookService;

    @BeforeEach
    void setUp() {
        bookService = new BookServiceImpl(bookRepository);
    }

    @Test
    @DisplayName("Thêm sách mới thành công")
    void addBook_success() {
        CreateBookRequest request = new CreateBookRequest("978-0132350884", "Clean Code", "Robert C. Martin", "IT");
        when(bookRepository.findByIsbn("978-0132350884")).thenReturn(Optional.empty());
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> {
            Book b = invocation.getArgument(0);
            b.setId(1L);
            return b;
        });

        Book savedBook = bookService.addBook(request);

        assertNotNull(savedBook);
        assertEquals(1L, savedBook.getId());
        assertEquals("Clean Code", savedBook.getTitle());
        assertEquals(BookStatus.AVAILABLE, savedBook.getStatus());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    @DisplayName("Thêm sách thất bại khi trùng ISBN")
    void addBook_duplicateIsbn_throwsException() {
        CreateBookRequest request = new CreateBookRequest("978-0132350884", "Clean Code", "Robert C. Martin", "IT");
        Book existing = new Book("978-0132350884", "Old Book", "Author", "IT");
        when(bookRepository.findByIsbn("978-0132350884")).thenReturn(Optional.of(existing));

        assertThrows(BusinessException.class, () -> bookService.addBook(request));
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("Tìm sách theo ID thành công")
    void getBookById_found() {
        Book book = new Book("978-1111", "Refactoring", "Martin Fowler", "IT");
        book.setId(10L);
        when(bookRepository.findById(10L)).thenReturn(Optional.of(book));

        Book result = bookService.getBookById(10L);

        assertEquals("Refactoring", result.getTitle());
    }

    @Test
    @DisplayName("Tìm sách không tồn tại ném ResourceNotFoundException")
    void getBookById_notFound_throwsException() {
        when(bookRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.getBookById(999L));
    }

    @Test
    @DisplayName("Lấy danh sách sách khả dụng")
    void getAvailableBooks() {
        when(bookRepository.findByStatus(BookStatus.AVAILABLE)).thenReturn(List.of(
                new Book("111", "Book 1", "Author 1", "Tech"),
                new Book("222", "Book 2", "Author 2", "Tech")
        ));

        List<Book> books = bookService.getAvailableBooks();

        assertEquals(2, books.size());
        verify(bookRepository, times(1)).findByStatus(BookStatus.AVAILABLE);
    }
}
