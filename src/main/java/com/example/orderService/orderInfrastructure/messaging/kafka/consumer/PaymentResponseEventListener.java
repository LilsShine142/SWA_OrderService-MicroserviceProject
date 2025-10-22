package com.example.orderService.orderInfrastructure.messaging.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentResponseEventListener {
    @KafkaListener(topics = "payment-response", groupId = "order-service-group")
    public void listen(String message) {
        // Logic xử lý phản hồi thanh toán
    }
}