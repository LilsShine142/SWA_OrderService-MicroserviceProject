package com.example.payment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO (Command) để tạo thanh toán.
 */
public record CreatePaymentCommand(
        @NotNull UUID orderId,
        @NotNull UUID customerId,
        @NotNull @Positive BigDecimal amount
) {}