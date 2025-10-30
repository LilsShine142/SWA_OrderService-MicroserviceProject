package com.example.payment.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * DTO (Response) cho việc hủy thanh toán.
 */
public record CancelPaymentResponse(
        @NotNull UUID paymentId,
        @NotNull String status,
        @NotNull String message
) {}