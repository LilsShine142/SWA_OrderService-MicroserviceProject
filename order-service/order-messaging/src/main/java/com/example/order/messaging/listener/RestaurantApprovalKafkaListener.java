package com.example.order.messaging.listener;

import com.example.common_messaging.dto.event.OrderApprovedEvent;
import com.example.common_messaging.dto.event.OrderRejectedEvent;
import com.example.order.application.ports.input.service.OrderApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestaurantApprovalKafkaListener {

    private final OrderApplicationService orderApplicationService;

    // Nhận khi Restaurant DUYỆT đơn
    @KafkaListener(
            topics = "order-approved",
            groupId = "order-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleOrderApproved(OrderApprovedEvent event) {
        System.out.println("NHẬN OrderApprovedEvent → OrderId: {} được nhà hàng DUYỆT!"+ event.getOrderId());

        try {
            orderApplicationService.approveOrder(event.getOrderId());
            System.out.println("HOÀN TẤT duyệt đơn → OrderId: {} chuyển sang trạng thái APPROVED!"+ event.getOrderId());
        } catch (Exception e) {
            log.error("LỖI khi duyệt đơn OrderId={}: {}", event.getOrderId(), e.getMessage(), e);
            throw e; // Để Kafka retry nếu cần
        }
    }

    // Nhận khi Restaurant TỪ CHỐI đơn
    @KafkaListener(
            topics = "order-rejected",
            groupId = "order-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleOrderRejected(OrderRejectedEvent event) {
        System.out.println("NHẬN OrderRejectedEvent → OrderId: {} bị nhà hàng TỪ CHỐI! Lý do: {}"+
                event.getOrderId()+ event.getReason());

        try {
            orderApplicationService.rejectOrder(event.getOrderId(), event.getReason());
            System.out.println("HOÀN TẤT từ chối đơn → OrderId: {} chuyển sang trạng thái REJECTED!"+ event.getOrderId());
        } catch (Exception e) {
            log.error("LỖI khi từ chối đơn OrderId={}: {}", event.getOrderId(), e.getMessage(), e);
            throw e;
        }
    }
}