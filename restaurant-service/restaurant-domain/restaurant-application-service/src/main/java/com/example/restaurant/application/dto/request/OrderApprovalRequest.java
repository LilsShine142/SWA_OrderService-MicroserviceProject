package com.example.restaurant.application.dto.request;

import com.example.restaurant.application.dto.request.ApproveOrderCommand.OrderItemDto;
import java.util.List;
import java.util.UUID;

public record OrderApprovalRequest(
        UUID orderId,
        UUID restaurantId,
//        UUID trackingId,
        List<OrderItemDto> items
) {
    public OrderApprovalRequest {
        if (orderId == null) {
            throw new IllegalArgumentException("orderId và sagaId là bắt buộc");
        }
    }
}