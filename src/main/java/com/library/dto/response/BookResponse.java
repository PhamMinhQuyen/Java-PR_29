package com.library.dto.response;

import com.library.domain.model.Book;
import com.library.domain.model.BookStatus;

import java.time.LocalDateTime;

public class BookResponse {

    private Long id;
    private String isbn;
    private String title;
    private String author;
    private String category;
    private BookStatus status;
    private LocalDateTime createdAt;

    public BookResponse() {
    }

    public static BookResponse fromEntity(Book book) {
        if (book == null) return null;
        BookResponse res = new BookResponse();
        res.id = book.getId();
        res.isbn = book.getIsbn();
        res.title = book.getTitle();
        res.author = book.getAuthor();
        res.category = book.getCategory();
        res.status = book.getStatus();
        res.createdAt = book.getCreatedAt();
        return res;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BookStatus getStatus() {
        return status;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
