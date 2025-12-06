package com.example.order.messaging.listener;

import com.example.common_messaging.dto.request.OrderRequestEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    @KafkaListener(
            topics = "order-request",
            groupId = "order-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(@Payload OrderRequestEvent event) {
        try {
            processAction(event);
        } catch (Exception e) {
            log.error("Error processing action: {}", event.getAction(), e);
        }
    }

    private void processAction(OrderRequestEvent event) {
        // Implementation of action processing
    }
}
