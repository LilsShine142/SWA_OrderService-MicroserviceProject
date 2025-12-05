package com.example.payment.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO (Command) để tạo thanh toán.
 */
@Getter
@Setter
public class CreatePaymentCommand {
        private UUID orderId;
        private UUID customerId;
        private BigDecimal amount;
        private LocalDateTime createdAt;
}