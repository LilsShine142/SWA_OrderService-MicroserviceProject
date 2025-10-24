package com.example.order.application.dto;

import com.example.order.domain.core.valueobject.OrderStatus;
import jakarta.validation.constraints.NotNull;

/**
 * DTO (Response) cho việc hủy đơn hàng.
 */
public record CancelOrderResponse(
        @NotNull OrderStatus orderStatus,
        @NotNull String message
) {}