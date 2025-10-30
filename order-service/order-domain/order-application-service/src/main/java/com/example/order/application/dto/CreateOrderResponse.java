package com.example.order.application.dto;

import com.example.order.domain.core.valueobject.OrderStatus;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * DTO (Response) cho việc tạo đơn hàng.
 */
public record CreateOrderResponse(
        @NotNull UUID orderTrackingId, // [cite: 257]
        @NotNull OrderStatus orderStatus,
        @NotNull String message
) {}