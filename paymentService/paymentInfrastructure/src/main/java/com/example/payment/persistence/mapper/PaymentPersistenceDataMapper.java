package com.example.payment.persistence.mapper;

import com.example.payment.entity.Payment;
import com.example.payment.persistence.entity.PaymentEntity;
import com.example.payment.valueobject.*;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

/**
 * Mapper chuyển đổi giữa Payment (Domain) và PaymentEntity (Infrastructure)
 */
@Component
public class PaymentPersistenceDataMapper {

    /**
     * Chuyển từ Domain Payment sang PaymentEntity (để lưu vào DB)
     */
    public PaymentEntity paymentToPaymentEntity(Payment payment) {
        return new PaymentEntity(
                payment.getId(),
                payment.getOrderId().value(),
                payment.getCustomerId().value(),
                payment.getAmount(),
                payment.getPaymentStatus(),
                payment.getTransactionId(),
                payment.getFailureReason(),
                payment.getCreatedAt() != null ?
                        payment.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime() : null,
                payment.getUpdatedAt() != null ?
                        payment.getUpdatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime() : null,
                payment.getTransactionStartAt(),
                payment.getTransactionEndAt(),
                payment.getSagaId(),
                payment.getSagaStep(),
                payment.getSagaStatus(),
                payment.getAttemptCount(),
                payment.getNextRetryAt()
        );
    }

    /**
     * Chuyển từ PaymentEntity (DB) sang Domain Payment
     */
    public Payment paymentEntityToPayment(PaymentEntity entity) {
        return Payment.builder()
                .paymentId(new PaymentId(entity.getId()))
                .orderId(new OrderId(entity.getOrderId()))
                .customerId(new CustomerId(entity.getCustomerId()))
                .amount(entity.getAmount())
                .paymentStatus(entity.getPaymentStatus())
                .transactionId(entity.getTransactionId())
                .failureReason(entity.getFailureReason())
                .transactionStartAt(entity.getTransactionStartAt())
                .transactionEndAt(entity.getTransactionEndAt())
                .createdAt(entity.getCreatedAt() != null ?
                        entity.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant() : null)
                .updatedAt(entity.getUpdatedAt() != null ?
                        entity.getUpdatedAt().atZone(ZoneId.systemDefault()).toInstant() : null)
                .sagaId(entity.getSagaId())
                .sagaStep(entity.getSagaStep())
                .sagaStatus(entity.getSagaStatus())
                .attemptCount(entity.getAttemptCount())
                .nextRetryAt(entity.getNextRetryAt())
                .build();
    }
}