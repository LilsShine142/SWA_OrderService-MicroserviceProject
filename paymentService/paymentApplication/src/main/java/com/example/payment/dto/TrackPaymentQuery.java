package com.example.payment.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * DTO (Query) để theo dõi thanh toán.
 */
public record TrackPaymentQuery(
        @NotNull UUID paymentId
) {}