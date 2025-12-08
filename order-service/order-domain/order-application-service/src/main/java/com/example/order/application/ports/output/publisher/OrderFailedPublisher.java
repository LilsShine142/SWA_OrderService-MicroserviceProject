package com.example.order.application.ports.output.publisher;

import com.example.order.application.dto.OrderFailedEvent;
import com.example.order.application.dto.OrderPaidEvent;

public interface OrderFailedPublisher {
    void publish(OrderFailedEvent domainEvent);
}
