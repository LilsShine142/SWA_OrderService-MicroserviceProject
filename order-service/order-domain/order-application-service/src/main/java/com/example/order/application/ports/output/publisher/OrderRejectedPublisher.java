package com.example.order.application.ports.output.publisher;

import com.example.common_messaging.dto.event.OrderRejectedEvent;

public interface OrderRejectedPublisher {
    void publish(OrderRejectedEvent domainEvent);
}
