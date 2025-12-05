package com.example.payment.exception;

public class PaymentValidationException extends PaymentDomainException {
    public PaymentValidationException(String message) {
        super(message);
    }
}