package com.example.payment.messaging.kafka.consumer;

import com.example.common_messaging.dto.event.OrderCreatedEvent;
import com.example.common_messaging.dto.event.OrderRejectedEvent;
import com.example.payment.dto.OrderEvent;
import com.example.payment.ports.input.service.PaymentApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentKafkaListener {

    private final PaymentApplicationService paymentApplicationService;

    // 1. Lắng nghe sự kiện Order Created (Khớp với KafkaOrderCreatedPublisher bên Order Service)
    @KafkaListener(topics = "order-created", groupId = "payment-service-group")
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("💰 [PAYMENT-SERVICE] Nhận event OrderCreated: orderId={}, amount={}",
                event.getOrderId(), event.getTotalAmount());

        try {
            // Tạo OrderEvent để cập nhật cache status
            OrderEvent orderEvent = new OrderEvent(
                    event.getOrderId(),
                    event.getCustomerId(),
                    event.getTotalAmount(),
                    event.getStatus() // Lấy status từ event gửi từ order
            );

            // Gọi hàm cập nhật cache status của order
            paymentApplicationService.processPaymentFromEvent(orderEvent);

            log.info("✅ Đã cập nhật cache status cho orderId: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("❌ Lỗi khi xử lý OrderCreatedEvent: {}", e.getMessage());
            // Có thể thêm logic gửi event "PaymentFailed" ngược lại Order Service tại đây
        }
    }

    // 2. Lắng nghe sự kiện Order Rejected (Để hoàn tiền nếu cần)
    @KafkaListener(topics = "order-rejected", groupId = "payment-service-group")
    public void handleOrderRejected(OrderRejectedEvent event) {
        log.info("💰 [PAYMENT-SERVICE] Nhận event OrderRejected: orderId={}, reason={}",
                event.getOrderId(), event.getReason());

        try {
            // Gọi refund theo orderId
            paymentApplicationService.refundPayment(event.getOrderId(), event.getReason());
            log.info("✅ Đã thực hiện refund cho orderId: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("❌ Lỗi xử lý OrderRejectedEvent: {}", e.getMessage());
        }
    }
}