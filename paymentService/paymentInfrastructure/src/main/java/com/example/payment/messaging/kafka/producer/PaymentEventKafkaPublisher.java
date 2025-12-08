//package com.example.payment.messaging.kafka.producer;
//
//import com.example.common_messaging.dto.event.PaymentCompletedEvent;
//import com.example.common_messaging.dto.event.PaymentFailedEvent;
//import com.example.payment.entity.Payment;
//import com.example.payment.event.PaymentDomainEvent;
//import com.example.payment.ports.output.MessagePaymentEventPublisher;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.support.SendResult;
//import org.springframework.stereotype.Component;
//
//import java.util.concurrent.CompletableFuture;
//
///**
// * Kafka Publisher for Payment Events
// */
//@Slf4j
//@Component
//public class PaymentEventKafkaPublisher implements MessagePaymentEventPublisher {
//
//    private final KafkaTemplate<String, Object> kafkaTemplate;
//
//    public PaymentEventKafkaPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
//        this.kafkaTemplate = kafkaTemplate;
//    }
//
//    @Override
//    public void publish(PaymentDomainEvent<Payment> event) {
//        String topicName = getTopicName(event);
//        String key = event.getPayload().getId().toString();
//
//        log.info("Publishing event {} to topic {} with key {}", event.getClass().getSimpleName(), topicName, key);
//
//        try {
//            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topicName, key, event);
//            future.whenComplete((result, ex) -> {
//                if (ex == null) {
//                    log.info("Event {} published successfully to topic {} partition {} offset {}",
//                            event.getClass().getSimpleName(), topicName, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
//                } else {
//                    log.error("Failed to publish event {} to topic {}", event.getClass().getSimpleName(), topicName, ex);
//                }
//            });
//        } catch (Exception e) {
//            log.error("Error publishing event {} to topic {}", event.getClass().getSimpleName(), topicName, e);
//        }
//    }
//
//    // === BẮN INTEGRATION EVENT RA NGOÀI (mới thêm) ===
//    public void publishPaymentCompleted(PaymentCompletedEvent event) {
//        System.out.println("BẮN RA NGOÀI: PaymentCompletedIntegrationEvent cho orderId={}"+ event.getOrderId());
//        sendToKafka("payment-completed", event.getOrderId().toString(), event);
//    }
//
//    public void publishPaymentFailed(PaymentFailedEvent event) {
//        System.out.println("BẮN RA NGOÀI: PaymentFailedIntegrationEvent cho orderId={} | Lý do: {}" +
//                event.getOrderId() + event.getReason());
//        sendToKafka("payment-failed", event.getOrderId().toString(), event);
//    }
//
//    // === Helper method để không lặp code ===
//    private <T> void sendToKafka(String topic, String key, T event) {
//        try {
//            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, key, event);
//            future.whenComplete((result, ex) -> {
//                if (ex == null) {
//                    System.out.println("BẮN THÀNH CÔNG {} → topic: {} | partition: {} | offset: {}" +
//                            event.getClass().getSimpleName() + topic +
//                            result.getRecordMetadata().partition() + result.getRecordMetadata().offset());
//                } else {
//                    log.error("BẮN THẤT BẠI {} → topic: {}", event.getClass().getSimpleName(), topic, ex);
//                }
//            });
//        } catch (Exception e) {
//            log.error("LỖI GỬI KAFKA: {}", e.getMessage(), e);
//        }
//    }
//
//    private String getTopicName(PaymentDomainEvent<Payment> event) {
//        if (event instanceof com.example.payment.event.PaymentCreatedEvent) {
//            return "payment-created";
//        } else if (event instanceof com.example.payment.event.PaymentCompletedEvent) {
//            return "payment-completed";
//        } else if (event instanceof com.example.payment.event.PaymentFailedEvent) {
//            return "payment-failed";
//        } else if (event instanceof com.example.payment.event.PaymentRefundedEvent) {
//            return "payment-refunded";
//        } else {
//            return "payment-events";
//        }
//    }
//}




// File: payment-service/src/main/java/com/example/payment/messaging/kafka/producer/PaymentEventKafkaPublisher.java

package com.example.payment.messaging.kafka.producer;

import com.example.payment.entity.Payment;
import com.example.common_messaging.dto.event.PaymentCompletedEvent;
import com.example.common_messaging.dto.event.PaymentFailedEvent;
import com.example.payment.event.PaymentDomainEvent;
import com.example.payment.ports.output.MessagePaymentEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventKafkaPublisher implements MessagePaymentEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    // Bắn Domain Event nội bộ
    @Override
    public void publish(PaymentDomainEvent<Payment> event) {
        String topic = getTopicName(event);
        String key = event.getPayload().getId().toString();
        send(topic, key, event, "Domain Event");
    }

    // Bắn Integration Event ra ngoài – THẤT BẠI
    @Override
    public void publish(PaymentFailedEvent event) {
        System.out.println("BẮN RA NGOÀI: PaymentFailedEvent cho orderId={}"+ event.getOrderId());
        send("payment-failed", event.getOrderId().toString(), event, "Integration Event - FAILED");
    }

    // Helper method chung
    private <T> void send(String topic, String key, T payload, String type) {
        log.info("BẮN {} → topic: {} | key: {}", type, topic, key);
        kafkaTemplate.send(topic, key, payload)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("THÀNH CÔNG {} → topic: {} | partition: {} | offset: {}",
                                type, topic, result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
                    } else {
                        log.error("THẤT BẠI {} → topic: {}", type, topic, ex);
                    }
                });
    }

    private String getTopicName(PaymentDomainEvent<Payment> event) {
        if (event instanceof com.example.payment.event.PaymentCompletedEvent) return "payment-completed";
        if (event instanceof com.example.payment.event.PaymentFailedEvent) return "payment-failed";
        return "payment-events";
    }
}