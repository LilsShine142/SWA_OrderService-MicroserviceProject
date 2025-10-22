package com.example.orderService.orderApplication.ports;

import com.example.orderService.orderDomain.event.OrderCancelledEvent;
import com.example.orderService.orderDomain.event.OrderCreatedEvent;
import com.example.orderService.orderDomain.event.OrderPaidEvent;

public interface OrderEventPublisher {
    void publish(OrderCreatedEvent event);
    void publish(OrderCancelledEvent event);
    void publish(OrderPaidEvent event);
}