package com.example.order.messaging.publisher;

import com.example.order.application.dto.OrderPaidEvent;
import com.example.order.application.ports.output.publisher.OrderPaidPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaOrderPaidPublisher implements OrderPaidPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    // Topic này Restaurant Service sẽ lắng nghe
    private static final String TOPIC_ORDER_PAID = "order-paid";

    @Override
    public void publish(OrderPaidEvent event) {
        try {
            log.info("📢 Gửi event OrderPaid sang Restaurant: orderId={}", event.getOrderId());
            kafkaTemplate.send(TOPIC_ORDER_PAID, event);
        } catch (Exception e) {
            log.error("❌ Lỗi khi gửi event OrderPaid: {}", e.getMessage());
        }
    }
}