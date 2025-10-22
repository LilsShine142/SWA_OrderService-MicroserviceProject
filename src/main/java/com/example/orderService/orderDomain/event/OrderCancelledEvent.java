package com.example.orderService.orderDomain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public class OrderCancelledEvent {
    private final UUID orderId;
    private final LocalDateTime cancelledAt;

    public OrderCancelledEvent(UUID orderId) {
        this.orderId = orderId;
        this.cancelledAt = LocalDateTime.now();
    }

    public UUID getOrderId() { return orderId; }
    public LocalDateTime getCancelledAt() { return cancelledAt; }
}