package com.example.payment.exception;

/**
 * Exception khi compensation (bù trừ) trong SAGA thất bại
 */
public class SagaCompensationException extends PaymentDomainException {
    public SagaCompensationException(String message) {
        super(message);
    }
    public SagaCompensationException(String message, Throwable cause) {
        super(message, cause);
    }
}