package com.example.restaurant.application.ports.output.publisher;

import com.example.common_messaging.dto.event.OrderRejectedEvent;

public interface OrderRejectedEventPublisher {
    void publish(OrderRejectedEvent event);
}