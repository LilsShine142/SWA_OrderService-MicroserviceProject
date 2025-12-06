package com.example.order.messaging.publisher;

import com.example.order.domain.core.event.OrderCancelledEvent;
import com.example.order.application.ports.output.publisher.OrderCancelledEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogOnlyOrderCancelledEventPublisher implements OrderCancelledEventPublisher {

    @Override
    public void publish(OrderCancelledEvent event) {
        log.info("Publishing OrderCancelledEvent: {}", event);
    }
}
