package com.example.payment.persistence.mapper;

import com.example.payment.entity.Payment;
import com.example.payment.persistence.entity.PaymentEntity;
import com.example.payment.valueobject.*;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.UUID;

/**
 * Mapper chuyển đổi giữa Payment (Domain) và PaymentEntity (Infrastructure)
 */
@Component
public class PaymentPersistenceDataMapper {
    /**
     * Chuyển từ Domain Payment sang PaymentEntity (để lưu vào DB)
     */
    public PaymentEntity paymentToPaymentEntity(Payment payment) {
        PaymentEntity entity = new PaymentEntity();
        entity.setId(payment.getId().value());
        entity.setOrderId(payment.getOrderId().value());
        entity.setCustomerId(payment.getCustomerId().value());
        entity.setAmount(payment.getPrice().getAmount());
        entity.setPaymentStatus(PaymentStatus.valueOf(payment.getPaymentStatus().name())); // STRING since no ENUM in DB
        entity.setTransactionId(payment.getTransactionId());
        entity.setFailureReason(payment.getFailureReason());
        entity.setCreatedAt(payment.getCreatedAt());
        entity.setUpdatedAt(payment.getUpdatedAt());
        entity.setTransactionStartAt(payment.getTransactionStartAt());
        entity.setTransactionEndAt(payment.getTransactionEndAt());
        return entity;
    }

    public Payment paymentEntityToPayment(PaymentEntity entity) {
        Payment payment = Payment.builder()
                .orderId(new OrderId(entity.getOrderId()))
                .customerId(new CustomerId(entity.getCustomerId()))
                .price(new Money(entity.getAmount()))
                .paymentStatus(PaymentStatus.valueOf(String.valueOf(entity.getPaymentStatus())))
                .transactionId(entity.getTransactionId())
                .failureReason(entity.getFailureReason())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .transactionStartAt(entity.getTransactionStartAt())
                .transactionEndAt(entity.getTransactionEndAt())
                .build();
        // 2. Set trường của cha (ID) thủ công
        payment.setId(new PaymentId(entity.getId()));
        return payment;
    }
}