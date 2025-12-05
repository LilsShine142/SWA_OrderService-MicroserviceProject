package com.example.payment.mapper;

import com.example.payment.dto.*;
import com.example.payment.valueobject.CustomerId;
import com.example.payment.valueobject.Money;
import com.example.payment.valueobject.OrderId;
import com.example.payment.entity.Payment;
import com.example.payment.valueobject.PaymentStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class PaymentDataMapper {

    public Payment paymentRequestToPayment(CreatePaymentCommand request) {
        return Payment.builder()
                .orderId(new OrderId(request.getOrderId()))
                .customerId(new CustomerId(request.getCustomerId()))
                .price(new Money(request.getAmount()))
                .paymentStatus(PaymentStatus.PENDING)
                .createdAt(request.getCreatedAt())
                .build();
    }

    public PaymentResponse paymentToPaymentResponse(Payment payment, String message, String paymentUrl) {
        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(payment.getId().value());
        response.setOrderId(payment.getOrderId());
        response.setCustomerId(payment.getCustomerId());
        response.setAmount(payment.getPrice().getAmount());
        response.setStatus(payment.getPaymentStatus());
        response.setTransactionId(payment.getTransactionId());
        response.setFailureReason(payment.getFailureReason());
        response.setCreatedAt(payment.getCreatedAt());
        response.setUpdatedAt(payment.getUpdatedAt());
        response.setMessage(message);
        response.setPaymentUrl(paymentUrl);
        return response;
    }
}