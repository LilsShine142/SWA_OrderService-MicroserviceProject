package com.example.order.messaging.publisher;

import com.example.order.domain.core.event.OrderApprovedEvent;
import com.example.order.application.ports.output.publisher.OrderApprovedEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LogOnlyOrderApprovedEventPublisher implements OrderApprovedEventPublisher {

    @Override
    public void publish(OrderApprovedEvent event) {
        log.info("Publishing OrderApprovedEvent: {}", event);
    }
}
