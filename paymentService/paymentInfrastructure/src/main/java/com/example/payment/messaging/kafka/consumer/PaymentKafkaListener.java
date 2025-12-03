package com.example.payment.messaging.kafka.consumer;

import com.example.common_messaging.dto.event.OrderCreatedEvent;
import com.example.common_messaging.dto.event.OrderRejectedEvent;
import com.example.payment.dto.CreatePaymentCommand;
import com.example.payment.ports.input.service.PaymentApplicationService;
import org.springframework.kafka.annotation.KafkaListener;

public class PaymentKafkaListener {
    private final PaymentApplicationService paymentApplicationService;

    public PaymentKafkaListener(PaymentApplicationService paymentApplicationService) {
        this.paymentApplicationService = paymentApplicationService;
    }

    @KafkaListener(topics = "order-created", groupId = "payment-group")
    public void handleOrderCreated(OrderCreatedEvent event) {
        CreatePaymentCommand request = new CreatePaymentCommand();
        request.setOrderId(event.getOrderId());
        request.setCustomerId(event.getCustomerId());
        request.setAmount(event.getTotalAmount());
        paymentApplicationService.processPayment(request);
    }

    @KafkaListener(topics = "order-rejected", groupId = "payment-group")
    public void handleOrderRejected(OrderRejectedEvent event) {
        // Assume event has paymentId, transactionNo
        paymentApplicationService.refundPayment(event.getOrderId(), String.valueOf(event.getRestaurantId()), event.getReason());
    }
}
