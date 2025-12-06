package com.example.order.messaging.publisher;

import com.example.order.domain.core.event.OrderUpdatedEvent;
import com.example.order.application.ports.output.publisher.OrderUpdatedEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaOrderUpdatedEventPublisher implements OrderUpdatedEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publish(OrderUpdatedEvent event) {
        try {
            kafkaTemplate.send("order-updated", event);
            log.info("Published OrderUpdatedEvent to Kafka: {}", event);
        } catch (Exception e) {
            log.error("Failed to publish OrderUpdatedEvent: {}", event, e);
        }
    }
}
