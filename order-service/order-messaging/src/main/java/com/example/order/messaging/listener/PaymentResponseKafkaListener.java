package com.example.order.messaging.listener;

import com.example.common_messaging.dto.event.PaymentCompletedEvent;
import com.example.common_messaging.dto.event.PaymentFailedEvent;
import com.example.order.application.ports.input.service.OrderApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentResponseKafkaListener {

    private final OrderApplicationService orderApplicationService;

    // Nghe tin nhắn từ Payment Service
    @KafkaListener(topics = "payment-completed", groupId = "order-service-group", containerFactory = "paymentCompletedKafkaListenerContainerFactory")
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        System.out.println("📥 Nhận event PaymentCompleted cho Order: {}"+ event.getOrderId() + event.getStatus());

        // Gọi Service xử lý
        orderApplicationService.payOrder(event.getOrderId());
    }

    @KafkaListener(
            topics = "payment-failed",
            groupId = "order-service-group",
            containerFactory = "paymentFailedKafkaListenerContainerFactory" // <--- QUAN TRỌNG
    )
    public void handlePaymentFailed(PaymentFailedEvent event) {
        log.warn("NHẬN PaymentFailedIntegrationEvent – OrderId: {}, Lý do: {}",
                event.getOrderId(), event.getReason());

        orderApplicationService.failOrder(event.getOrderId(), event.getReason());
    }
}
