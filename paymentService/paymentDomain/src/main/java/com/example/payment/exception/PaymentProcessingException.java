package com.example.payment.exception;

public class PaymentProcessingException extends PaymentDomainException {
    public PaymentProcessingException(String message) {
        super(message);
    }
}