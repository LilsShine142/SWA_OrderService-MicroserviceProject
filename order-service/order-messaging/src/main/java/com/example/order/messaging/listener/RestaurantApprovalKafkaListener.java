package com.example.order.messaging.listener;

import com.example.common_messaging.dto.event.OrderApprovedEvent;
import com.example.order.application.ports.input.service.OrderApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class RestaurantApprovalKafkaListener {

    private final OrderApplicationService orderApplicationService;

    // Nghe tin nhắn từ Restaurant Service
    @KafkaListener(topics = "order-approved", groupId = "order-service-group")
    public void handleOrderApproved(OrderApprovedEvent event) {
        log.info("📥 Nhận event OrderApproved cho Order: {}", event.getOrderId());

        // Gọi Service xử lý
        orderApplicationService.approveOrder(event.getOrderId());
    }
}