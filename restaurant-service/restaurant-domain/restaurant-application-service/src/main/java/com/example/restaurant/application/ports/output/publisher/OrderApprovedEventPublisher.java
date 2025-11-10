package com.example.restaurant.application.ports.output.publisher;

import com.example.common_messaging.dto.event.OrderApprovedEvent;

public interface OrderApprovedEventPublisher {
    void publish(OrderApprovedEvent event);
}
