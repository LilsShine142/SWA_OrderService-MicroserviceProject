package com.example.payment.mapper;

import com.example.payment.dto.*;
import com.example.payment.valueobject.CustomerId;
import com.example.payment.valueobject.OrderId;
import com.example.payment.valueobject.PaymentSagaStatus;
import com.example.payment.entity.Payment;
import com.example.payment.valueobject.PaymentStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Class để ánh xạ (mapping) giữa DTO và Domain Entity cho Payment Service
 */
@Component
public class PaymentDataMapper {

    /**
     * Chuyển đổi CreatePaymentCommand thành Payment Domain Entity
     */
    public Payment createPaymentCommandToPayment(CreatePaymentCommand command) {
        return Payment.builder()
                .orderId(new OrderId(command.orderId()))
                .customerId(new CustomerId(command.customerId()))
                .amount(command.amount())
                .paymentStatus(PaymentStatus.PENDING)
                .sagaId(UUID.randomUUID().toString())
                .sagaStatus(PaymentSagaStatus.STARTED)
                .sagaStep("PAYMENT_INITIATED")
                .attemptCount(0)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    /**
     * Chuyển đổi Payment Domain Entity thành CreatePaymentResponse
     */
    public CreatePaymentResponse paymentToCreatePaymentResponse(Payment payment, String message) {
        return new CreatePaymentResponse(
                payment.getId(),
                payment.getPaymentStatus().name(),
                message
        );
    }

    /**
     * Chuyển đổi Payment Domain Entity thành CancelPaymentResponse
     */
    public CancelPaymentResponse paymentToCancelPaymentResponse(Payment payment, String message) {
        return new CancelPaymentResponse(
                payment.getId(),
                payment.getPaymentStatus().name(),
                message
        );
    }

    /**
     * Chuyển đổi Payment Domain Entity thành TrackPaymentResponse
     */
    public TrackPaymentResponse paymentToTrackPaymentResponse(Payment payment) {
        return new TrackPaymentResponse(
                payment.getId(),
                payment.getOrderId(),
                payment.getCustomerId(),
                payment.getAmount(),
                payment.getPaymentStatus().name(),
                payment.getTransactionId(),
                payment.getFailureReason(),
                payment.getCreatedAt() != null ?
                        payment.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : null,
                payment.getUpdatedAt() != null ?
                        payment.getUpdatedAt().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : null,
                payment.getSagaStep(),
                payment.getSagaStatus() != null ? payment.getSagaStatus().name() : null,
                payment.getAttemptCount()
        );
    }

    /**
     * Chuyển đổi Payment Domain Entity thành PaymentResponse
     */
    public PaymentResponse paymentToPaymentResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrderId(),
                payment.getCustomerId(),
                payment.getAmount(),
                payment.getPaymentStatus().name(),
                payment.getTransactionId(),
                payment.getFailureReason(),
                payment.getCreatedAt() != null ?
                        payment.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : null,
                payment.getUpdatedAt() != null ?
                        payment.getUpdatedAt().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : null,
                payment.getSagaStep(),
                payment.getSagaStatus() != null ? payment.getSagaStatus().name() : null
        );
    }

    /**
     * Chuyển đổi TrackPaymentQuery thành paymentId
     */
    public UUID trackPaymentQueryToPaymentId(TrackPaymentQuery query) {
        return query.paymentId();
    }

    /**
     * Chuyển đổi CancelPaymentCommand thành paymentId
     */
    public UUID cancelPaymentCommandToPaymentId(CancelPaymentCommand command) {
        return command.paymentId();
    }

    /**
     * Chuyển đổi CancelPaymentCommand thành failureReason
     */
    public String cancelPaymentCommandToFailureReason(CancelPaymentCommand command) {
        return command.failureReason();
    }
}