package com.example.order.messaging.publisher; // Package của file này

// THÊM DÒNG IMPORT NÀY
import com.example.order.application.ports.output.publisher.OrderCreatedPaymentRequestPublisher;

import com.example.order.domain.core.event.OrderCreatedEvent;
import org.springframework.stereotype.Component;

/**
 * Temporary implementation - Does nothing.
 * Used when Kafka is not yet configured.
 * 
 * NOTE: This is now disabled in favor of KafkaOrderCreatedPublisher.
 * Remove @Component to disable this implementation.
 */
// @Component  // Disabled - using KafkaOrderCreatedPublisher instead
public class NoOpOrderCreatedPublisher implements OrderCreatedPaymentRequestPublisher {
//                                              ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
//                                              Bây giờ Java mới biết interface này là gì

    @Override
    public void publish(OrderCreatedEvent orderCreatedEvent) {
        System.out.println(">>> Skipping Kafka publish for OrderCreatedEvent: " +
                orderCreatedEvent.getPayload().getId().value());
    }
}