package com.example.orderService.orderDomain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public class OrderCreatedEvent {
    private final UUID orderId;
    private final LocalDateTime createdAt;

    public OrderCreatedEvent(UUID orderId) {
        this.orderId = orderId;
        this.createdAt = LocalDateTime.now();
    }

    public UUID getOrderId() { return orderId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}