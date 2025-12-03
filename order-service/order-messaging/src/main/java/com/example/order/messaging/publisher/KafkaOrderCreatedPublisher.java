package com.example.order.messaging.publisher;

import com.example.common_messaging.dto.event.OrderCreatedEvent;
import com.example.order.application.ports.output.publisher.OrderCreatedPaymentRequestPublisher;
import com.example.order.domain.core.entity.Order;
import com.example.order.domain.core.entity.OrderItem;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Primary
@Component
public class KafkaOrderCreatedPublisher implements OrderCreatedPaymentRequestPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC_ORDER_CREATED = "order-created";

    public KafkaOrderCreatedPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(com.example.order.domain.core.event.OrderCreatedEvent domainEvent) {
        try {
            Order order = domainEvent.getPayload();
            
            // Convert Domain Event to Messaging DTO
            OrderCreatedEvent messagingEvent = convertToMessagingEvent(order);
            
            // Publish to Kafka
            kafkaTemplate.send(TOPIC_ORDER_CREATED, messagingEvent);
            
            System.out.println("[KAFKA] Published OrderCreatedEvent: orderId=" + messagingEvent.getOrderId() + 
                    ", customerId=" + messagingEvent.getCustomerId() + 
                    ", totalAmount=" + messagingEvent.getTotalAmount());
                    
        } catch (Exception e) {
            System.err.println("Error publishing OrderCreatedEvent to Kafka: orderId=" + 
                    domainEvent.getPayload().getId().value() + ", error: " + e.getMessage());
            throw new RuntimeException("Failed to publish OrderCreatedEvent to Kafka", e);
        }
    }

    /**
     * Convert Domain Order entity to Messaging DTO
     * This is the adapter pattern - converting between domain and messaging layers
     */
    private OrderCreatedEvent convertToMessagingEvent(Order order) {
        // Generate sagaId for event tracking (if not present in domain)
        UUID sagaId = UUID.randomUUID();
        
        // Convert OrderItems to DTOs
        List<OrderCreatedEvent.OrderItemDto> itemDtos = order.getItems().stream()
                .map(this::convertOrderItemToDto)
                .collect(Collectors.toList());
        
        return OrderCreatedEvent.builder()
                .orderId(order.getId().value())
                .customerId(order.getCustomerId().value())
                .restaurantId(order.getRestaurantId().value())
                .totalAmount(order.getPrice().getAmount())
                .items(itemDtos)
                .build();
    }

    /**
     * Convert Domain OrderItem to Messaging DTO
     */
    private OrderCreatedEvent.OrderItemDto convertOrderItemToDto(OrderItem orderItem) {
        return OrderCreatedEvent.OrderItemDto.builder()
                .productId(orderItem.getProductId().value())
                .productName(null) // Domain doesn't have product name, can be enriched later
                .quantity(orderItem.getQuantity())
                .price(orderItem.getPrice().getAmount())
                .build();
    }
}

