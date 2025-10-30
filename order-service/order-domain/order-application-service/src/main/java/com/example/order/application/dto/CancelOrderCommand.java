package com.example.order.application.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * DTO (Command) để hủy đơn hàng.
 */
public record CancelOrderCommand(
        @NotNull UUID orderTrackingId
) {}