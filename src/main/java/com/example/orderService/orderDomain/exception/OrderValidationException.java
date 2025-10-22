package com.example.orderService.orderDomain.exception;

public class OrderValidationException extends OrderDomainException {
    public OrderValidationException(String message) {
        super(message);
    }
}