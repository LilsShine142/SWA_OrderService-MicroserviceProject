package com.example.restaurant.messaging.listener;

import com.example.common_messaging.dto.event.OrderApprovedEvent;
import com.example.common_messaging.dto.event.OrderPaidEvent;
import com.example.common_messaging.dto.event.OrderRejectedEvent;
import com.example.restaurant.application.dto.request.ApproveOrderCommand;
import com.example.restaurant.application.ports.input.service.RestaurantApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

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
            ApproveOrderCommand command = ApproveOrderCommand.builder()
                    .orderId(orderEvent.getOrderId())
                    .restaurantId(orderEvent.getRestaurantId())
//                    .sagaId(orderEvent.getSagaId())
                    .items(orderEvent.getItems().stream()
                            .map(item -> new ApproveOrderCommand.OrderItemDto(
                                    item.getProductId(),
                                    item.getQuantity(),
                                    item.getPrice()
                            ))
                            .collect(Collectors.toList()))
                    .build();

            restaurantService.approveOrder(command);
            System.out.println("Duyệt đơn thành công: orderId=" + orderEvent.getOrderId());

            // Publish OrderApprovedEvent
            OrderApprovedEvent approvedEvent = OrderApprovedEvent.builder()
                    .orderId(orderEvent.getOrderId())
                    .restaurantId(orderEvent.getRestaurantId())
                    .items(orderEvent.getItems().stream()
                            .map(item -> OrderApprovedEvent.OrderItemDto.builder()
                                    .productId(item.getProductId())
                                    .productName(item.getProductName())
                                    .quantity(item.getQuantity())
                                    .price(item.getPrice())
                                    .build())
                            .collect(Collectors.toList()))
                    .build();
            kafkaTemplate.send("order-approved", approvedEvent);
            System.out.println("Đã publish OrderApprovedEvent: orderId=" + orderEvent.getOrderId());

        } catch (Exception e) {
            System.out.println("Lỗi duyệt đơn orderId=" + orderEvent.getOrderId() + ": " + e.getMessage());
            publishCompensationEvent(orderEvent, e.getMessage());
        }
    }

    private void publishCompensationEvent(OrderPaidEvent event, String reason) {
        try {
            OrderRejectedEvent compensationEvent = OrderRejectedEvent.builder()
                    .orderId(event.getOrderId())
//                    .sagaId(event.getSagaId())
                    .restaurantId(event.getRestaurantId())
                    .reason(reason)
                    .build();

            kafkaTemplate.send("order-rejected", compensationEvent);
            System.out.println("Đã publish OrderRejectedEvent (compensation): orderId=" + event.getOrderId());
        } catch (Exception e) {
            System.out.println("Lỗi publish compensation event: " + e.getMessage());
        }
    }
}