//package com.example.restaurant.messaging.listener;
//
//
//import com.example.common_messaging.dto.event.OrderPaidEvent;
//import com.example.restaurant.application.ports.output.repository.RestaurantRepositoryPort;
//import com.example.restaurant.domain.core.entity.OrderApproval;
//import com.example.restaurant.domain.core.valueobject.ApprovalStatus;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//
//import java.util.UUID;
//
//@Component
//@Slf4j
//@RequiredArgsConstructor
//public class OrderPaidKafkaListener {
//
//    private final RestaurantRepositoryPort restaurantRepositoryPort;
//
//    @KafkaListener(topics = "order-paid", groupId = "restaurant-service-group")
//    public void handleOrderPaid(OrderPaidEvent event) {
//        log.info("🔔 [RESTAURANT] Có đơn mới đã thanh toán! OrderID: {}", event.getOrderId());
//
//        // Lưu vào DB để chủ quán thấy
//        OrderApproval orderApproval;
//        orderApproval = OrderApproval.builder()
//                .id(UUID.randomUUID())
//                .restaurantId(event.getRestaurantId())
//                .orderId(event.getOrderId())
//                .status(ApprovalStatus.PENDING) // Mặc định là chờ duyệt
//                .build();
//
//        restaurantRepositoryPort.save(orderApproval);
//        log.info("📝 Đã lưu đơn hàng vào danh sách chờ duyệt.");
//    }
//}

package com.example.restaurant.messaging.listener;

import com.example.common_messaging.dto.event.OrderPaidEvent;
import com.example.restaurant.application.ports.input.service.RestaurantApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderPaidKafkaListener {

    private final RestaurantApplicationService restaurantApplicationService;

    @KafkaListener(topics = "order-paid", groupId = "restaurant-service-group")
    public void handleOrderPaid(OrderPaidEvent event) {
        log.info("🔔 [Messaging Adapter] Nhận tin nhắn OrderPaid: {}", event.getOrderId());

        // Chuyển tiếp vào lớp Application Core xử lý
        restaurantApplicationService.completeOrderApproval(event);
    }
}