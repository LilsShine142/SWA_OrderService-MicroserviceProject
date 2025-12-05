package com.example.payment.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * DTO (Response) cho việc tạo thanh toán.
 */
public record CreatePaymentResponse(
        @NotNull UUID paymentId,
        @NotNull String status,
        @NotNull String message,
        @NotNull String paymentUrl
) {}