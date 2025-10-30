package com.example.payment.exception;

public class PaymentNotFoundException extends PaymentDomainException {
    public PaymentNotFoundException(String message) {
        super(message);
    }
}
