package com.example.restaurant.application.ports.output.publisher;

//import com.example.common_messaging.dto.event.OrderApprovedEvent;

import com.example.restaurant.domain.core.event.OrderApprovedEvent;

public interface MessageRestaurantPublisherPort {
    void publish(Object event);
}
