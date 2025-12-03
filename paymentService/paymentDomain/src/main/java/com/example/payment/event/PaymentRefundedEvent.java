package com.example.payment.event;

import com.example.payment.entity.Payment;

/**
 * Sự kiện được bắn ra khi một thanh toán được hoàn tiền thành công.
 */
public class PaymentRefundedEvent extends PaymentDomainEvent<Payment> {

    public PaymentRefundedEvent(Payment payment) {
        super(payment);
    }

    @Override
    public String getEventType() {
        return "PaymentRefunded";
    }

    @Override
    public String toString() {
        return "PaymentRefundedEvent{" +
                "payload=" + getPayload() +
                ", createdAt=" + getCreatedAt() +
                '}';
    }
}