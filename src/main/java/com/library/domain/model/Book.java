package com.library.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String isbn;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 100)
    private String author;

    @Column(length = 50)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Book() {
        this.status = BookStatus.AVAILABLE;
        this.createdAt = LocalDateTime.now();
    }

    public Book(String isbn, String title, String author, String category) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.category = category;
        this.status = BookStatus.AVAILABLE;
        this.createdAt = LocalDateTime.now();
    }

    public Book(Long id, String isbn, String title, String author, String category, BookStatus status) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.category = category;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    // Nghiệp vụ thuộc trách nhiệm của Book
    public boolean isAvailable() {
        return BookStatus.AVAILABLE.equals(this.status);
    }

    public void markAsBorrowed() {
        if (!isAvailable()) {
            throw new IllegalStateException("Cuốn sách '" + title + "' hiện không có sẵn để mượn.");
        }
        this.status = BookStatus.BORROWED;
    }

    public void markAsAvailable() {
        this.status = BookStatus.AVAILABLE;
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
