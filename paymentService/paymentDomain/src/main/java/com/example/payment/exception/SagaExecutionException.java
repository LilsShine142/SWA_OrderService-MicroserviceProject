package com.example.payment.exception;

/**
 * Exception khi thực thi SAGA thất bại
 */
public class SagaExecutionException extends PaymentDomainException {
    public SagaExecutionException(String message) {
        super(message);
    }
    public SagaExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
