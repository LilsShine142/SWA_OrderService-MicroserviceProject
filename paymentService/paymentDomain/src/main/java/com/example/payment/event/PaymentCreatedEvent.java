package com.example.payment.event;

import com.example.payment.entity.Payment;

/**
 * Sự kiện được bắn ra khi một thanh toán được tạo thành công.
 */
public class PaymentCreatedEvent extends PaymentDomainEvent<Payment> {

    public PaymentCreatedEvent(Payment payment) {
        super(payment);
    }

    @Override
    public String getEventType() {
        return "";
    }

    @Override
    public String toString() {
        return "PaymentCreatedEvent{" +
                "payload=" + getPayload() +
                ", createdAt=" + getCreatedAt() +
                '}';
    }
}