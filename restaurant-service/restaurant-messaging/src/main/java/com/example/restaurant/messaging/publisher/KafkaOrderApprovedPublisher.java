package com.example.restaurant.messaging.publisher;

import com.example.common_messaging.dto.event.OrderApprovedEvent;
import com.example.restaurant.application.ports.output.publisher.OrderApprovedEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaOrderApprovedPublisher implements OrderApprovedEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publish(OrderApprovedEvent event) {
        try {
            kafkaTemplate.send("order-approved", event.getOrderId().toString(), event);
            System.out.println("[KAFKA] Published OrderApprovedEvent: orderId=" + event.getOrderId());
        } catch (Exception e) {
            System.out.println("Lỗi publish OrderApprovedEvent: " + e.getMessage());
            throw new RuntimeException("Lỗi khi publish OrderApprovedEvent", e);
        }
    }
}