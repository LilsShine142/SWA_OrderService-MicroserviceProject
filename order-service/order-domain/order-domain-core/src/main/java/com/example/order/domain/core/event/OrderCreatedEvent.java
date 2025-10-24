package com.example.order.domain.core.event;



import com.example.order.domain.core.entity.Order;

/**
 * Sự kiện được bắn ra khi một Order được tạo thành công.
 */
public class OrderCreatedEvent extends DomainEvent<Order> {

    // Hàm khởi tạo này nhận Order (payload)
    public OrderCreatedEvent(Order order) {
        super(order);
    }
}