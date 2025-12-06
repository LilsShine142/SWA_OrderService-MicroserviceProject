package com.example.order.domain.core.event;

import com.example.order.domain.core.entity.Order;

/**
 * Sự kiện được bắn ra khi một Order được duyệt.
 */
public class OrderApprovedEvent extends DomainEvent<Order> {

    public OrderApprovedEvent(Order order) {
        super(order);
    }
}
