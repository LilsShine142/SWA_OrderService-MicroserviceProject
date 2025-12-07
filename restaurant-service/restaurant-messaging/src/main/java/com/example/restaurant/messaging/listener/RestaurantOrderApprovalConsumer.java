package com.example.restaurant.messaging.listener;

import com.example.common_messaging.dto.event.OrderPaidEvent;
import com.example.restaurant.application.ports.input.service.RestaurantApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantOrderApprovalConsumer {

    private final RestaurantApplicationService restaurantService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(
            topics = "order-paid",
            groupId = "restaurant-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeOrderPaid(OrderPaidEvent orderEvent) {
        System.out.println("Nhận OrderPaidEvent: orderId=" + orderEvent.getOrderId());

        try {
            // Tự động thêm order vào bảng OrderApproval với status PENDING
            restaurantService.completeOrderApproval(orderEvent);
            System.out.println("Đã thêm order vào danh sách chờ duyệt: orderId=" + orderEvent.getOrderId());
        } catch (Exception e) {
            System.out.println("Lỗi thêm order orderId=" + orderEvent.getOrderId() + ": " + e.getMessage());
        }
    }
}