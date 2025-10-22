package com.example.orderService.orderDomain.exception;

public class OrderNotFoundException extends OrderDomainException {
    public OrderNotFoundException(String message) {
        super(message);
    }
}