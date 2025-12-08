package com.example.restaurant.messaging.listener;

import com.example.common_messaging.dto.event.OrderPaidEvent;
import com.example.restaurant.application.ports.input.service.RestaurantApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestaurantOrderApprovalConsumer {

    private final RestaurantApplicationService restaurantService;

    @KafkaListener(
            topics = "order-paid",
            groupId = "restaurant-service-group",  // PHẢI GIỐNG TRÊN
            containerFactory = "kafkaListenerContainerFactory"  // ĐÚNG TÊN BEAN
    )
    public void consumeOrderPaid(OrderPaidEvent event) {
        System.out.println("[RESTAURANT] NHẬN OrderPaidEvent → orderId: {}, status: {}" +
                event.getOrderId() + event.getStatus());

        if (!"PAID".equals(event.getStatus())) {
            log.warn("Order không phải PAID → bỏ qua: {}", event.getOrderId());
            return;
        }

        try {
            restaurantService.completeOrderApproval(event);
            System.out.println("ĐÃ XỬ LÝ THÀNH CÔNG – OrderId: {} được thêm vào chờ duyệt!"+ event.getOrderId());
        } catch (Exception e) {
            System.out.println("LỖI XỬ LÝ OrderPaidEvent orderId={}: {}"+ event.getOrderId()+ e.getMessage()+ e);
            throw e; // Để Kafka retry nếu cần
        }
    }
}