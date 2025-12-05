package com.example.payment.dto;

import com.example.payment.valueobject.CustomerId;
import com.example.payment.valueobject.OrderId;
import com.example.payment.valueobject.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO (Response) cho thông tin thanh toán.
 */
@Getter
@Setter
public class PaymentResponse implements Serializable {
    private UUID paymentId;
    private OrderId orderId;
    private CustomerId customerId;
    private BigDecimal amount;
    private PaymentStatus status;
    private String transactionId;
    private String failureReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // Set for response
    private String message;
    private String paymentUrl; // For VNPay
}