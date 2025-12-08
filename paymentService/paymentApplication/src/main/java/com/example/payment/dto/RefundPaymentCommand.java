package com.example.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor // <--- QUAN TRỌNG: Để Jackson khởi tạo object
@AllArgsConstructor
public class RefundPaymentCommand {
    private UUID paymentId;
    private UUID orderId;
    private double amount;
    private String transactionNo;
    private String reason;
    private String transactionType;  // e.g., "02" for partial refund per VNPay
}