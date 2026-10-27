package com.library.exception;

/**
 * Base exception cho các lỗi nghiệp vụ trong hệ thống thư viện.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
