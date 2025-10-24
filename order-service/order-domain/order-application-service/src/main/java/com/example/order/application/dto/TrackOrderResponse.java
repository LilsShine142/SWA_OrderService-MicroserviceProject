package com.example.order.application.dto;

import com.example.order.domain.core.valueobject.OrderStatus;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

/**
 * DTO (Response) cho việc theo dõi đơn hàng.
 */
public record TrackOrderResponse(
        @NotNull UUID orderTrackingId,
        @NotNull OrderStatus orderStatus,
        List<String> failureMessages
        // (Bạn có thể thêm các trường khác như:
        //  List<OrderItemDTO> items, Instant createdAt,...)
) {}