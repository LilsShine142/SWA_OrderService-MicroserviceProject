package com.example.restaurant.messaging.publisher;

import com.example.restaurant.application.ports.output.publisher.OrderRejectedEventPublisher;
import com.example.common_messaging.dto.event.OrderRejectedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaOrderRejectedPublisher implements OrderRejectedEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publish(OrderRejectedEvent event) {
        try {
            kafkaTemplate.send("order-rejected", event);
            System.out.println("[KAFKA] Published OrderRejectedEvent: orderId=" + event.getOrderId() +
                    ", reason=" + event.getReason());
        } catch (Exception e) {
            System.out.println("Lỗi publish OrderRejectedEvent: " + e.getMessage());
        }
    }
}