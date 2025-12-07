package com.example.order.messaging.listener;

import com.example.common_messaging.dto.event.PaymentCompletedEvent;
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
    @KafkaListener(topics = "payment-completed", groupId = "order-service-group")
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        log.info("📥 Nhận event PaymentCompleted cho Order: {}", event.getOrderId());

        // Gọi Service xử lý
        orderApplicationService.payOrder(event.getOrderId());
    }
}
