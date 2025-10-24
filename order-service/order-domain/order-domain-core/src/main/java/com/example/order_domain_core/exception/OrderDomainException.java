package com.example.orderDomain.exception;


/**
 * Exception tùy chỉnh cho các lỗi nghiệp vụ trong domain.
 * Đây là một Unchecked Exception.
 */
public class OrderDomainException extends RuntimeException {

    public OrderDomainException(String message) {
        super(message);
    }

    public OrderDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
