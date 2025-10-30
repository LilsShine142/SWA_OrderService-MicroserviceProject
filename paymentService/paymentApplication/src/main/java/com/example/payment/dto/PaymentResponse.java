package com.example.payment.dto;

import com.example.payment.valueobject.CustomerId;
import com.example.payment.valueobject.OrderId;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO (Response) cho thông tin thanh toán.
 */
public record PaymentResponse(
        @NotNull UUID paymentId,
        @NotNull OrderId orderId,
        @NotNull CustomerId customerId,
        @NotNull BigDecimal amount,
        @NotNull String status,
        String transactionId,
        String failureReason,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String sagaStep,
        String sagaStatus
) {}