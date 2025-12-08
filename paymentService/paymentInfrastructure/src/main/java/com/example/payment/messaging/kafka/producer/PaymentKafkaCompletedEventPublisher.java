package com.example.payment.messaging.kafka.producer;

import com.example.common_messaging.dto.event.PaymentCompletedEvent;
import com.example.payment.ports.output.PaymentCompletedEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentKafkaCompletedEventPublisher implements PaymentCompletedEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC_ORDER_CREATED = "payment-completed";

    // Bắn Integration Event ra ngoài – THÀNH CÔNG
    @Override
    public void publish(PaymentCompletedEvent event) {
        try {
            PaymentCompletedEvent fullEvent = PaymentCompletedEvent.builder()
                    .orderId(event.getOrderId())
                    .paymentId(event.getPaymentId())
                    .customerId(event.getCustomerId())
                    .transactionId(event.getTransactionId())
                    .amount(event.getAmount())
                    .restaurantId(event.getRestaurantId())
                    .status(event.getStatus())
                    .items(event.getItems() != null ? event.getItems() : new ArrayList<>()) // ← DÒNG QUAN TRỌNG!
                    .build();
            // Publish to Kafka
            kafkaTemplate.send(TOPIC_ORDER_CREATED, fullEvent);

            System.out.println("[KAFKA] Published PaymentCompletedEvent: orderId=" + event.getOrderId() +
                    ", customerId=" + event.getCustomerId() +
                    ", status=" + event.getStatus() +
                    ", amount=" + event.getAmount());

        } catch (Exception e) {
            System.err.println("Error publishing PaymentCompletedEvent to Kafka: orderId=" +
                    event.getOrderId() + ", error: " + e.getMessage());
            throw new RuntimeException("Failed to publish PaymentCompletedEvent to Kafka", e);
        }
    }
}