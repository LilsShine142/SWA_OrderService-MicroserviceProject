package com.example.payment.messaging.kafka.producer;

import com.example.payment.entity.Payment;
import com.example.payment.event.PaymentDomainEvent;
import com.example.payment.ports.output.MessagePaymentEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Kafka Publisher for Payment Events
 */
@Slf4j
@Component
public class PaymentEventKafkaPublisher implements MessagePaymentEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PaymentEventKafkaPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(PaymentDomainEvent<Payment> event) {
        String topicName = getTopicName(event);
        String key = event.getPayload().getId().toString();

        log.info("Publishing event {} to topic {} with key {}", event.getClass().getSimpleName(), topicName, key);

        try {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topicName, key, event);
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Event {} published successfully to topic {} partition {} offset {}",
                            event.getClass().getSimpleName(), topicName, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to publish event {} to topic {}", event.getClass().getSimpleName(), topicName, ex);
                }
            });
        } catch (Exception e) {
            log.error("Error publishing event {} to topic {}", event.getClass().getSimpleName(), topicName, e);
        }
    }

    private String getTopicName(PaymentDomainEvent<Payment> event) {
        if (event instanceof com.example.payment.event.PaymentCreatedEvent) {
            return "payment-created";
        } else if (event instanceof com.example.payment.event.PaymentCompletedEvent) {
            return "payment-completed";
        } else if (event instanceof com.example.payment.event.PaymentFailedEvent) {
            return "payment-failed";
        } else if (event instanceof com.example.payment.event.PaymentRefundedEvent) {
            return "payment-refunded";
        } else {
            return "payment-events";
        }
    }
}
