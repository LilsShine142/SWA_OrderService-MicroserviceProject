package com.example.payment.persistence.entity;

import com.example.payment.valueobject.PaymentStatus;
import com.example.payment.valueobject.PaymentSagaStatus;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Entity map với bảng "payments" trong CSDL.
 * Lưu UUID trực tiếp thay vì Value Object để tương thích với JPA
 */
@Entity
@Table(name = "payments", schema = "payment")
public class PaymentEntity {

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

    @Column(name = "saga_id")
    private String sagaId;

    @Column(name = "saga_step")
    private String sagaStep;

    @Enumerated(EnumType.STRING)
    @Column(name = "saga_status")
    private PaymentSagaStatus sagaStatus;

    @Column(name = "attempt_count")
    private Integer attemptCount;

    @Column(name = "next_retry_at")
    private LocalDateTime nextRetryAt;

    // Constructor rỗng (bắt buộc cho JPA)
    public PaymentEntity() {}

    // Constructor đầy đủ
    public PaymentEntity(UUID id, UUID orderId, UUID customerId, BigDecimal amount,
                         PaymentStatus paymentStatus, String transactionId, String failureReason,
                         LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime transactionStartAt,
                         LocalDateTime transactionEndAt, String sagaId, String sagaStep,
                         PaymentSagaStatus sagaStatus, Integer attemptCount, LocalDateTime nextRetryAt) {
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
        this.sagaId = sagaId;
        this.sagaStep = sagaStep;
        this.sagaStatus = sagaStatus;
        this.attemptCount = attemptCount;
        this.nextRetryAt = nextRetryAt;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getTransactionStartAt() {
        return transactionStartAt;
    }

    public void setTransactionStartAt(LocalDateTime transactionStartAt) {
        this.transactionStartAt = transactionStartAt;
    }

    public LocalDateTime getTransactionEndAt() {
        return transactionEndAt;
    }

    public void setTransactionEndAt(LocalDateTime transactionEndAt) {
        this.transactionEndAt = transactionEndAt;
    }

    public String getSagaId() {
        return sagaId;
    }

    public void setSagaId(String sagaId) {
        this.sagaId = sagaId;
    }

    public String getSagaStep() {
        return sagaStep;
    }

    public void setSagaStep(String sagaStep) {
        this.sagaStep = sagaStep;
    }

    public PaymentSagaStatus getSagaStatus() {
        return sagaStatus;
    }

    public void setSagaStatus(PaymentSagaStatus sagaStatus) {
        this.sagaStatus = sagaStatus;
    }

    public Integer getAttemptCount() {
        return attemptCount;
    }

    public void setAttemptCount(Integer attemptCount) {
        this.attemptCount = attemptCount;
    }

    public LocalDateTime getNextRetryAt() {
        return nextRetryAt;
    }

    public void setNextRetryAt(LocalDateTime nextRetryAt) {
        this.nextRetryAt = nextRetryAt;
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