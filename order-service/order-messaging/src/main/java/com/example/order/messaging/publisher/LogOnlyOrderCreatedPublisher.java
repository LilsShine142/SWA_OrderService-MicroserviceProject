package com.example.order.messaging.publisher;



import com.example.order.application.ports.output.publisher.OrderCreatedPaymentRequestPublisher;
import com.example.order.domain.core.event.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Temporary implementation - Only logs the event.
 * Used when Kafka is not yet configured.
 */
@Component
public class LogOnlyOrderCreatedPublisher implements OrderCreatedPaymentRequestPublisher {

    private static final Logger LOG = LoggerFactory.getLogger(LogOnlyOrderCreatedPublisher.class);

    @Override
    public void publish(OrderCreatedEvent orderCreatedEvent) {
        // Just log that the event would have been published
        LOG.info(">>> Would publish OrderCreatedEvent to Kafka (but Kafka is disabled): Order ID = {}",
                orderCreatedEvent.getPayload().getId().value());
    }
}