package com.example.restaurant.messaging.publisher;

import com.example.restaurant.domain.core.event.OrderApprovedEvent;
import com.example.restaurant.domain.core.event.OrderRejectedEvent;
import com.example.restaurant.application.ports.output.publisher.MessageRestaurantPublisherPort;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaMessagePublisher implements MessageRestaurantPublisherPort {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaMessagePublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(Object event) {
        String topic;
        if (event instanceof OrderApprovedEvent) {
            topic = "order-approved";
        } else if (event instanceof OrderRejectedEvent) {
            topic = "order-rejected";
        } else {
            throw new IllegalArgumentException("Unknown event type: " + event.getClass());
        }
        kafkaTemplate.send(topic, event);
    }
}