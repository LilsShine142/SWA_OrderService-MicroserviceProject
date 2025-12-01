package com.example.payment.messaging.kafka.consumer;

import com.example.common_messaging.dto.event.OrderCreatedEvent;
import com.example.payment.dto.CreatePaymentCommand;
import com.example.payment.ports.input.service.PaymentApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Consumer để lắng nghe các Order events từ Order Service
 */
@Component
public class OrderEventConsumer {
    private static final Logger logger = LoggerFactory.getLogger(OrderEventConsumer.class);
    private final PaymentApplicationService paymentApplicationService;

    public OrderEventConsumer(PaymentApplicationService paymentApplicationService) {
        this.paymentApplicationService = paymentApplicationService;
    }

    /**
     * Lắng nghe OrderCreatedEvent - khi order được tạo, Payment Service sẽ tạo payment
     */
    @KafkaListener(
            topics = "order-created",
            groupId = "payment-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeOrderCreated(OrderCreatedEvent event) {
        logger.info("📥 Payment Service nhận OrderCreatedEvent: orderId={}, customerId={}, amount={}, sagaId={}",
                event.getOrderId(), event.getCustomerId(), event.getTotalAmount(), event.getSagaId());

        try {
            // 1. Validate event data
            if (event.getOrderId() == null || event.getCustomerId() == null || event.getTotalAmount() == null) {
                throw new IllegalArgumentException("OrderCreatedEvent thiếu thông tin bắt buộc");
            }

            // 2. Convert to Command for Application Layer
            CreatePaymentCommand command = new CreatePaymentCommand(
                    event.getOrderId(),
                    event.getCustomerId(),
                    event.getTotalAmount()
            );

            // 3. Call Use Case through Input Port
            paymentApplicationService.createPayment(command);

            logger.info("Payment Service đã tạo payment thành công cho orderId={}", event.getOrderId());

        } catch (IllegalArgumentException e) {
            logger.error("Lỗi validation cho OrderCreatedEvent: orderId={}, error={}", 
                    event.getOrderId(), e.getMessage());
        } catch (Exception e) {
            logger.error("Lỗi xử lý OrderCreatedEvent: orderId={}", event.getOrderId(), e);
        }
    }
}