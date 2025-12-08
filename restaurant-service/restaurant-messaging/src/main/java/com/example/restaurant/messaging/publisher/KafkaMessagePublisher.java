//package com.example.restaurant.messaging.publisher;
//
//import com.example.restaurant.domain.core.event.OrderApprovedEvent;
//import com.example.restaurant.domain.core.event.OrderRejectedEvent;
//import com.example.restaurant.application.ports.output.publisher.MessageRestaurantPublisherPort;
//import lombok.RequiredArgsConstructor;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.stereotype.Component;
//
//@Component
//public class KafkaMessagePublisher implements MessageRestaurantPublisherPort {
//    private final KafkaTemplate<String, Object> kafkaTemplate;
//
//    public KafkaMessagePublisher(KafkaTemplate<String, Object> kafkaTemplate) {
//        this.kafkaTemplate = kafkaTemplate;
//    }
//
//    @Override
//    public void publish(Object event) {
//        String topic;
//        if (event instanceof OrderApprovedEvent) {
//            topic = "order-approved";
//        } else if (event instanceof OrderRejectedEvent) {
//            topic = "order-rejected";
//        } else {
//            throw new IllegalArgumentException("Unknown event type: " + event.getClass());
//        }
//        kafkaTemplate.send(topic, event);
//    }
//}



package com.example.restaurant.messaging.publisher;

import com.example.common_messaging.dto.event.OrderApprovedEvent; // DTO chung
import com.example.restaurant.application.ports.output.publisher.MessageRestaurantPublisherPort;
//import com.example.restaurant.domain.core.event.OrderApprovedEvent;
import com.example.restaurant.domain.core.event.OrderRejectedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor // Lombok tự tạo Constructor cho các field final
public class KafkaMessagePublisher implements MessageRestaurantPublisherPort {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    // Định nghĩa tên Topic rõ ràng
    private static final String TOPIC_ORDER_APPROVED = "order-approved";
    private static final String TOPIC_ORDER_REJECTED = "order-rejected";

    @Override
    public void publish(Object domainEvent) {
        try {
            String topic = "";
            OrderApprovedEvent integrationEvent = null;

            // 1. Map Domain Event -> Integration Event (DTO)
            if (domainEvent instanceof OrderApprovedEvent event) {
                topic = TOPIC_ORDER_APPROVED;
                integrationEvent = OrderApprovedEvent.builder()
                        .orderId(event.getOrderId())
                        .status("APPROVED")
                        .build();
            } else if (domainEvent instanceof OrderRejectedEvent event) {
                topic = TOPIC_ORDER_REJECTED;
                integrationEvent = OrderApprovedEvent.builder()
                        .orderId(event.getApproval().getOrderId())
                        .status("REJECTED")
                        .build();
            } else {
                log.warn("⚠️ Unknown event type: {}", domainEvent.getClass().getName());
                return;
            }

            // 2. Gửi DTO ra Kafka
           System.out.println("📤 [RESTAURANT] Bắn event {} cho OrderId: {}" + topic +  integrationEvent.getOrderId());
            kafkaTemplate.send(topic, integrationEvent);

        } catch (Exception e) {
           System.out.println("❌ Lỗi khi bắn event Kafka: {}"+ e.getMessage());
            // Có thể throw exception để retry nếu cần
        }
    }
}