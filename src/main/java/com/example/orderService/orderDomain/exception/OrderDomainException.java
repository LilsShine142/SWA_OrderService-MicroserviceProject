package com.example.orderService.orderDomain.exception;

public class OrderDomainException extends RuntimeException {
    public OrderDomainException(String message) {
        super(message);
    }
}