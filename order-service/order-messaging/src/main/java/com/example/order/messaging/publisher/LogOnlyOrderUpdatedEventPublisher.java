package com.example.order.messaging.publisher;

import com.example.order.domain.core.event.OrderUpdatedEvent;
import com.example.order.application.ports.output.publisher.OrderUpdatedEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogOnlyOrderUpdatedEventPublisher implements OrderUpdatedEventPublisher {

    @Override
    public void publish(OrderUpdatedEvent event) {
        log.info("Publishing OrderUpdatedEvent: {}", event);
    }
}
