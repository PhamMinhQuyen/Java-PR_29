package com.library.exception;

/**
 * Ném ra khi không tìm thấy tài nguyên (Sách, Thành viên, Lượt mượn).
 */
public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String resourceName, Object identifier) {
        super(String.format("Không tìm thấy %s với định danh: '%s'", resourceName, identifier));
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
