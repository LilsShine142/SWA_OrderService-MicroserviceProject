package com.example.order.application.dto;

import com.example.order.domain.core.valueobject.OrderStatus;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * DTO (Response) cho việc cập nhật đơn hàng.
 */
public record UpdateOrderResponse(
        @NotNull UUID orderTrackingId,
        @NotNull OrderStatus orderStatus,
        @NotNull String message
) {}
