package com.library.config;

import com.library.domain.model.Book;
import com.library.domain.model.Member;
import com.library.domain.repository.BookRepository;
import com.library.domain.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

/**
 * Khởi tạo dữ liệu mẫu ban đầu để kiểm thử ngay khi ứng dụng chạy.
 */
@Configuration
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    public DataInitializer(BookRepository bookRepository, MemberRepository memberRepository) {
        this.bookRepository = bookRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    public void run(String... args) {
        if (bookRepository.findAll().isEmpty()) {
            bookRepository.save(new Book("978-0132350884", "Clean Code", "Robert C. Martin", "Software Engineering"));
            bookRepository.save(new Book("978-0134494166", "Clean Architecture", "Robert C. Martin", "Architecture"));
            bookRepository.save(new Book("978-0201633610", "Design Patterns: Elements of Reusable Object-Oriented Software", "Gang of Four", "Software Design"));
            bookRepository.save(new Book("978-0134685991", "Effective Java", "Joshua Bloch", "Java Programming"));
            log.info(">> Đã khởi tạo 4 cuốn sách mẫu vào thư viện.");
        }

        if (memberRepository.findAll().isEmpty()) {
            memberRepository.save(new Member("Pham Minh Quyen", "quyen@example.com", "0901234567"));
            memberRepository.save(new Member("Nguyen Van A", "thib@example.com", "0912345678"));
            log.info(">> Đã khởi tạo 2 thành viên độc giả mẫu.");
        }
    }
}
