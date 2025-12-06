package com.example.order.domain.core.event;

import com.example.order.domain.core.entity.Order;

/**
 * Sự kiện được bắn ra khi một Order được cập nhật.
 */
public class OrderUpdatedEvent extends DomainEvent<Order> {

    public OrderUpdatedEvent(Order order) {
        super(order);
    }
}
