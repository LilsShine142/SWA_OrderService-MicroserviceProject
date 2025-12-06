package com.example.payment.messaging.kafka.consumer;

import com.example.common_messaging.dto.event.OrderCreatedEvent;
import com.example.common_messaging.dto.event.OrderRejectedEvent;
import com.example.payment.dto.CreatePaymentCommand;
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
            // Chuyển đổi Event thành Command nội bộ của Payment
            CreatePaymentCommand command = CreatePaymentCommand.builder()
                    .orderId(event.getOrderId())
                    .customerId(event.getCustomerId())
                    .amount(event.getTotalAmount())
                    .build();

            // Gọi Service xử lý thanh toán
            paymentApplicationService.processPayment(command);

            log.info("✅ Đã khởi tạo thanh toán cho orderId: {}", event.getOrderId());
        } catch (Exception e) {
            log.error("❌ Lỗi khi xử lý OrderCreatedEvent: {}", e.getMessage());
            // Có thể thêm logic gửi event "PaymentFailed" ngược lại Order Service tại đây
        }
    }

    // 2. Lắng nghe sự kiện Order Rejected (Để hoàn tiền nếu cần)
    @KafkaListener(topics = "order-rejected", groupId = "payment-service-group")
    public void handleOrderRejected(OrderRejectedEvent event) {
        log.info("Received OrderRejectedEvent for order id: {}", event.getOrderId());
        // Giả sử logic hoàn tiền nằm ở đây
        paymentApplicationService.refundPayment(
                event.getOrderId(),
                String.valueOf(event.getRestaurantId()), // Cẩn thận kiểu dữ liệu chỗ này
                event.getReason()
        );
    }
}