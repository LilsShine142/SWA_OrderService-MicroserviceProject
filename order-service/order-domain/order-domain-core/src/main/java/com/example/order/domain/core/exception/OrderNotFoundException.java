package com.example.order.domain.core.exception;



/**
 * Exception ném ra khi không tìm thấy Order.
 */
public class OrderNotFoundException extends OrderDomainException {

    public OrderNotFoundException(String message) {
        super(message);
    }

    public OrderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}