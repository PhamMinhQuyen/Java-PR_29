package com.library.exception;

/**
 * Ném ra khi thao tác với lượt mượn không hợp lệ (ví dụ: cố gắng trả một cuốn sách đã được trả rồi).
 */
public class InvalidLoanStateException extends BusinessException {

    public InvalidLoanStateException(Long loanId, String reason) {
        super(String.format("Lượt mượn ID %d không hợp lệ: %s", loanId, reason));
    }

    public InvalidLoanStateException(String message) {
        super(message);
    }
}
