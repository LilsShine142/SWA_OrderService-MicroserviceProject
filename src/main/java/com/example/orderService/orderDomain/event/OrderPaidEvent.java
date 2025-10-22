package com.example.orderService.orderDomain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public class OrderPaidEvent {
    private final UUID orderId;
    private final LocalDateTime paidAt;

    public OrderPaidEvent(UUID orderId) {
        this.orderId = orderId;
        this.paidAt = LocalDateTime.now();
    }

    public UUID getOrderId() { return orderId; }
    public LocalDateTime getPaidAt() { return paidAt; }
}