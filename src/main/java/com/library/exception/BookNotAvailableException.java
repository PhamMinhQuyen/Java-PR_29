package com.library.exception;

/**
 * Ném ra khi sách không ở trạng thái AVAILABLE để cho mượn.
 */
public class BookNotAvailableException extends BusinessException {

    public BookNotAvailableException(Long bookId, String bookTitle) {
        super(String.format("Cuốn sách '%s' (ID: %d) hiện không khả dụng để mượn (đang được người khác mượn hoặc lưu trữ).",
                bookTitle, bookId));
    }

    public BookNotAvailableException(String message) {
        super(message);
    }
}
