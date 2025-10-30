package com.example.payment.ports.output;

public interface MessageBrokerOutputPort {
    void sendMessage(String topic, Object message);
    void sendPaymentApproved(String orderId, String paymentId);
    void sendPaymentFailed(String orderId, String reason);
}