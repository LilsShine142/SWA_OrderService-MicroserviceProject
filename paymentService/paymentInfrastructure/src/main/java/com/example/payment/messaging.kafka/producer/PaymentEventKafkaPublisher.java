package com.example.payment.messaging.kafka.producer;
import com.example.payment.ports.output.MessageBrokerOutputPort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
@Component
public class PaymentEventKafkaPublisher implements MessageBrokerOutputPort {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    public PaymentEventKafkaPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    @Override
    public void sendMessage(String topic, Object message) {
        kafkaTemplate.send(topic, message);
    }
    @Override
    public void sendPaymentApproved(String orderId, String paymentId) {
        sendMessage("payment-approved",
                new PaymentApprovedEvent(orderId, paymentId));
    }
    @Override
    public void sendPaymentFailed(String orderId, String reason) {
        sendMessage("payment-failed",
                new PaymentFailedEvent(orderId, reason));
    }
    // Event classes for Payment Service
    public static class PaymentApprovedEvent {
        private final String orderId;
        private final String paymentId;
        public PaymentApprovedEvent(String orderId, String paymentId) {
            this.orderId = orderId;
            this.paymentId = paymentId;
        }
        public String getOrderId() { return orderId; }
        public String getPaymentId() { return paymentId; }
    }
    public static class PaymentFailedEvent {
        private final String orderId;
        private final String reason;
        public PaymentFailedEvent(String orderId, String reason) {
            this.orderId = orderId;
            this.reason = reason;
        }
        public String getOrderId() { return orderId; }
        public String getReason() { return reason; }
    }
}