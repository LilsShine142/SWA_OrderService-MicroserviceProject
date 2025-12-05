package com.example.payment.exception;

import com.example.payment.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global Exception Handler - Presentation Layer
 * Chuyển đổi Domain Exceptions thành HTTP Responses
 * Tuân thủ Clean Architecture: Domain không biết về HTTP
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Xử lý Payment không tìm thấy
     */
    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handlePaymentNotFound(PaymentNotFoundException e) {
        log.error("Payment not found: {}", e.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    /**
     * Xử lý lỗi validation từ Domain
     */
    @ExceptionHandler(PaymentValidationException.class)
    public ResponseEntity<Map<String, Object>> handlePaymentValidation(PaymentValidationException e) {
        log.error("Payment validation failed: {}", e.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    /**
     * Xử lý lỗi xử lý payment (business logic)
     */
    @ExceptionHandler(PaymentProcessingException.class)
    public ResponseEntity<Map<String, Object>> handlePaymentProcessing(PaymentProcessingException e) {
        log.error("Payment processing failed: {}", e.getMessage());
        return buildErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
    }

    /**
     * Xử lý lỗi SAGA compensation
     */
    @ExceptionHandler(SagaCompensationException.class)
    public ResponseEntity<Map<String, Object>> handleSagaCompensation(SagaCompensationException e) {
        log.error("SAGA compensation failed: {}", e.getMessage());
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    /**
     * Xử lý tất cả các Domain Exceptions khác
     */
    @ExceptionHandler(PaymentDomainException.class)
    public ResponseEntity<Map<String, Object>> handlePaymentDomain(PaymentDomainException e) {
        log.error("Payment domain error: {}", e.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    /**
     * Xử lý UUID format không hợp lệ
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException e) {
        log.error("Invalid argument: {}", e.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST,
                "Invalid input format: " + e.getMessage());
    }

    /**
     * Xử lý lỗi validation từ @Valid annotation
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException e) {
        log.error("Validation error: {}", e.getMessage());

        String errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation failed: " + errors);
    }

    /**
     * Xử lý tất cả các exception chưa được catch
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception e) {
        log.error("Unexpected error: {}", e.getMessage(), e);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please try again later.");
    }

    /**
     * Helper method để build error response
     */
    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", status.value());
        errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("message", message);
        return ResponseEntity.status(status).body(errorResponse);
    }
}