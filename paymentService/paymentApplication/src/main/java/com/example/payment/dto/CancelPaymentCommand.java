package com.example.payment.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * DTO (Command) để hủy thanh toán.
 */
public record CancelPaymentCommand(
        @NotNull UUID paymentId,
        @NotNull String failureReason
) {}