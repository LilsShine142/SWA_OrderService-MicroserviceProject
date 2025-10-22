package com.example.orderService.orderInfrastructure.messaging.kafka.producer;

import com.example.orderService.orderApplication.ports.OrderEventPublisher;
import com.example.orderService.orderDomain.event.OrderCancelledEvent;
import com.example.orderService.orderDomain.event.OrderCreatedEvent;
import com.example.orderService.orderDomain.event.OrderPaidEvent;
import org.springframework.stereotype.Component;

@Component
public class OrderEventKafkaPublisher implements OrderEventPublisher {
    @Override
    public void publish(OrderCreatedEvent event) {
        // Logic publish to Kafka
    }

    @Override
    public void publish(OrderCancelledEvent event) {
        // Logic publish to Kafka
    }

    @Override
    public void publish(OrderPaidEvent event) {
        // Logic publish to Kafka
    }
}