package com.example.payment.entity;

import com.example.payment.valueobject.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Aggregate Root đại diện cho thực thể Payment trong Payment Service.
 */
@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
public class Payment extends AggregateRoot<PaymentId> {
    private final OrderId orderId;
    private final CustomerId customerId;
    private final Money price;
    private PaymentStatus paymentStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime transactionStartAt;
    private LocalDateTime transactionEndAt;
    private String transactionId;
    private String failureReason;

    public void initializePayment() {
        setId(new PaymentId(UUID.randomUUID()));
        createdAt = LocalDateTime.now();
        paymentStatus = PaymentStatus.PENDING;
        transactionStartAt = LocalDateTime.now();
    }

    public void validatePayment() {
        if (price == null || !price.isGreaterThanZero()) {
            throw new IllegalStateException("Payment amount must be greater than zero");
        }
    }

    public void complete() {
        if (paymentStatus != PaymentStatus.PENDING) {
            throw new IllegalStateException("Cannot complete non-pending payment");
        }
        paymentStatus = PaymentStatus.COMPLETED;
        transactionEndAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    public void fail(String reason) {
        if (paymentStatus != PaymentStatus.PENDING) {
            throw new IllegalStateException("Cannot fail non-pending payment");
        }
        paymentStatus = PaymentStatus.FAILED;
        failureReason = reason;
        transactionEndAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    public void refund() {
        if (paymentStatus != PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Cannot refund non-completed payment");
        }
        paymentStatus = PaymentStatus.REFUNDED;
        updatedAt = LocalDateTime.now();
    }

}