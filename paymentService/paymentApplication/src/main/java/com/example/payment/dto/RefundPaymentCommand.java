package com.example.payment.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class RefundPaymentCommand {
    private UUID paymentId;
    private UUID orderId;
    private double amount;
    private String transactionNo;
    private String reason;
    private String transactionType;  // e.g., "02" for partial refund per VNPay
}