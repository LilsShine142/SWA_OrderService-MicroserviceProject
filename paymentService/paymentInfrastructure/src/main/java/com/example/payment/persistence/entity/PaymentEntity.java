package com.example.payment.persistence.entity;

import com.example.payment.valueobject.PaymentStatus;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Entity map với bảng "payments" trong CSDL.
 * Lưu UUID trực tiếp thay vì Value Object để tương thích với JPA
 */
@Setter
@Getter
@Entity
@Table(name = "payment")
public class PaymentEntity {

    // Getters and Setters
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "transaction_start_at")
    private LocalDateTime transactionStartAt;

    @Column(name = "transaction_end_at")
    private LocalDateTime transactionEndAt;

    // Constructor rỗng (bắt buộc cho JPA)
    public PaymentEntity() {}

    // Constructor đầy đủ
    public PaymentEntity(UUID id, UUID orderId, UUID customerId, BigDecimal amount,
                         PaymentStatus paymentStatus, String transactionId, String failureReason,
                         LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime transactionStartAt,
                         LocalDateTime transactionEndAt) {
        this.id = id;
        this.orderId = orderId;
        this.customerId = customerId;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
        this.transactionId = transactionId;
        this.failureReason = failureReason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.transactionStartAt = transactionStartAt;
        this.transactionEndAt = transactionEndAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentEntity that = (PaymentEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}