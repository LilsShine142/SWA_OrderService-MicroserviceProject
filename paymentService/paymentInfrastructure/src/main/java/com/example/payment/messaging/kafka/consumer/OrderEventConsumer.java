package com.example.payment.messaging.kafka.consumer;

import com.example.payment.dto.OrderEvent;
import com.example.payment.ports.input.service.PaymentApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final PaymentApplicationService paymentService;

    @KafkaListener(topics = "order-events", groupId = "payment-service-group")
    public void consumeOrderEvent(OrderEvent event) {
        log.info("📥 Consumer nhận OrderEvent: {}", event.getOrderId());

        // Gọi thẳng hàm của Service
        paymentService.processPaymentFromEvent(event);
    }
}