package com.example.order.messaging.publisher;

import com.example.order.domain.core.event.OrderCancelledEvent;
import com.example.order.application.ports.output.publisher.OrderCancelledEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaOrderCancelledEventPublisher implements OrderCancelledEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publish(OrderCancelledEvent event) {
        try {
            kafkaTemplate.send("order-cancelled", event);
            log.info("Published OrderCancelledEvent to Kafka: {}", event);
        } catch (Exception e) {
            log.error("Failed to publish OrderCancelledEvent: {}", event, e);
        }
    }
}
