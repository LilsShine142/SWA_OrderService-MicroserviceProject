package com.example.restaurant.messaging.consumer;

import com.example.restaurant.application.ports.input.service.RestaurantApplicationService;
import com.example.restaurant.domain.core.event.PaymentCompletedEvent;
import com.example.restaurant.domain.core.event.PaymentFailedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantEventConsumer {
    private final RestaurantApplicationService restaurantService;

    @KafkaListener(
            topics = "payment-completed",
            containerFactory = "paymentCompletedKafkaListenerContainerFactory"
    )
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        System.out.println("📥 Nhận PaymentCompletedEvent: " + event);
        restaurantService.processOrderApproval(event);
    }

    @KafkaListener(
            topics = "payment-failed",
            containerFactory = "paymentFailedKafkaListenerContainerFactory"
    )
    public void handlePaymentFailed(PaymentFailedEvent event) {
        System.out.println("📥 Nhận PaymentFailedEvent: " + event);
        restaurantService.processPaymentFailed(event);
    }
}
