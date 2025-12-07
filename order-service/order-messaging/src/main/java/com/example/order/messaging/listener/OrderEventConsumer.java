package com.example.order.messaging.listener;

import com.example.common_messaging.dto.event.OrderApprovedEvent;
import com.example.common_messaging.dto.event.PaymentSuccessEvent;
import com.example.order.application.ports.input.service.OrderApplicationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderEventConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(OrderEventConsumer.class);

    private final OrderApplicationService orderService;

//    @KafkaListener(
//            topics = "payment-success",
//            groupId = "order-group",
//            containerFactory = "kafkaListenerContainerFactory"
//    )
//    public void consumePaymentSuccess(PaymentSuccessEvent event) {
//        LOG.info("Nhận PaymentSuccessEvent: orderId={}", event.getOrderId());
//        try {
//            orderService.processPaymentSuccess(event.getOrderId());
//            LOG.info("Đã xử lý payment success cho orderId={}", event.getOrderId());
//        } catch (Exception e) {
//            LOG.error("Lỗi xử lý payment success cho orderId={}: {}", event.getOrderId(), e.getMessage());
//        }
//    }

    @KafkaListener(
            topics = "order-approved",
            groupId = "order-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeOrderApproved(OrderApprovedEvent event) {
        LOG.info("Nhận OrderApprovedEvent: orderId={}", event.getOrderId());
        try {
            orderService.processRestaurantApproval(event.getOrderId());
            LOG.info("Đã xử lý restaurant approval cho orderId={}", event.getOrderId());
        } catch (Exception e) {
            LOG.error("Lỗi xử lý restaurant approval cho orderId={}: {}", event.getOrderId(), e.getMessage());
        }
    }
}

