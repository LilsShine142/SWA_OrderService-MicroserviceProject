package com.example.order.messaging.publisher;

import com.example.order.domain.core.event.OrderApprovedEvent;
import com.example.order.application.ports.output.publisher.OrderApprovedEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaOrderApprovedEventPublisher implements OrderApprovedEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publish(OrderApprovedEvent event) {
        try {
            kafkaTemplate.send("order-approved", event);
            log.info("Published OrderApprovedEvent to Kafka: {}", event);
        } catch (Exception e) {
            log.error("Failed to publish OrderApprovedEvent: {}", event, e);
        }
    }
}
