package com.example.payment.messaging.kafka.consumer;

import com.example.common_messaging.dto.event.OrderCreatedEvent;
import com.example.common_messaging.dto.event.OrderRejectedEvent;
import com.example.payment.dto.OrderEvent;
import com.example.payment.ports.input.service.PaymentApplicationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentKafkaListener {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final PaymentApplicationService paymentApplicationService;

    // 1. Lắng nghe sự kiện Order Created (Khớp với KafkaOrderCreatedPublisher bên Order Service)
    @KafkaListener(topics = "order-created", groupId = "payment-service-group")
    public void handleOrderCreated(OrderCreatedEvent event) {
        System.out.println("NHẬN THÀNH CÔNG OrderCreatedEvent TỪ KAFKA!!!");
        System.out.println("Order ID      : " + event.getOrderId());
        System.out.println("Customer ID   : " + event.getCustomerId());
        System.out.println("Total Amount  : " + event.getTotalAmount());
        System.out.println("Status        : " + event.getStatus());
        System.out.println("=".repeat(120));
        try {
            System.out.println("📥 [PAYMENT] Nhận OrderCreatedEvent: orderId=" + event.getOrderId() + ", status=" + event.getStatus());

//            2. Tạo Object để lưu vào Redis
            OrderCreatedEvent redisDto = OrderCreatedEvent.builder()
                    .orderId(event.getOrderId())
                    .customerId(event.getCustomerId())
                    .totalAmount(event.getTotalAmount())
                    .status(event.getStatus()) // Mặc định trạng thái ban đầu
                    .restaurantId(event.getRestaurantId())
                    .items(event.getItems())
                    .build();
            System.out.println("📝 Tạo Redis DTO: " + redisDto);
            // 3. Convert Object -> JSON String
            String jsonValue = objectMapper.writeValueAsString(redisDto);
            System.out.println("🔄 Chuyển đổi JSON String: " + jsonValue);
            // 4. Lưu vào Redis
            String key = "PAYMENT_ORDER:" + event.getOrderId();
            // TTL 30 phút
            redisTemplate.opsForValue().set(key, jsonValue, 30, TimeUnit.MINUTES);

            System.out.println("✅ Đã cache Object Order vào Redis: " + jsonValue);

            System.out.println("✅ Đã cache Order " + event.getOrderId() + " vào Redis (TTL 30p)");

        } catch (Exception e) {
            System.out.println("❌ Lỗi xử lý OrderCreatedEvent: " + e.getMessage() + e);
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