package com.example.order.application.ports.output.publisher;

import com.example.order.application.dto.OrderPaidEvent;

public interface OrderPaidPublisher {
    void publish(OrderPaidEvent domainEvent);
}
