package com.example.order.domain.core.event;

import com.example.order.domain.core.entity.Order;

/**
 * Sự kiện được bắn ra khi một Order bị hủy.
 */
public class OrderCancelledEvent extends DomainEvent<Order> {

    public OrderCancelledEvent(Order order) {
        super(order);
    }
}
