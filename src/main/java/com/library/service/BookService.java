package com.library.service;

import com.library.domain.model.Book;
import com.library.dto.request.CreateBookRequest;

import java.util.List;

/**
 * Interface nghiệp vụ quản lý Sách theo nguyên lý ISP (Interface Segregation Principle).
 * Chỉ tập trung vào các chức năng liên quan đến sách, không bị pha trộn với mượn/trả hay tính phí.
 */
public interface BookService {

    Book addBook(CreateBookRequest request);

    Book getBookById(Long id);

    List<Book> getAllBooks();

    List<Book> getAvailableBooks();
}
