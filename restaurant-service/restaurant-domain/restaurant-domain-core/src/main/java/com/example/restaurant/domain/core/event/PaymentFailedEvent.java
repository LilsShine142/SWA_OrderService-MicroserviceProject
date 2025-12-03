package com.example.restaurant.domain.core.event;

import com.example.restaurant.domain.core.entity.OrderItem;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Builder
@Data
public class PaymentFailedEvent {
    private UUID orderId;
    private UUID customerId;
    private UUID restaurantId;
    private List<OrderItem> items;
    private String failureReason;
}
