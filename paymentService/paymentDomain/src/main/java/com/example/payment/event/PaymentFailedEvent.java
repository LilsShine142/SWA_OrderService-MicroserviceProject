package com.example.payment.event;

import com.example.payment.entity.Payment;
import lombok.Getter;

@Getter
public class PaymentFailedEvent extends PaymentDomainEvent<Payment> {
    private final String failureMessage;
    public PaymentFailedEvent(Payment payment, String failureMessage) {
        super(payment);
        this.failureMessage = failureMessage;
    }

    @Override
    public String getEventType() {
        return "PaymentFailed";
    }

    @Override
    public String toString() {
        return "PaymentFailedEvent{" +
                "payload=" + getPayload() +
                ", createdAt=" + getCreatedAt() +
                '}';
    }
}
