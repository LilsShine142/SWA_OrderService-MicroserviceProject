package com.example.payment.event;

import com.example.payment.entity.Payment;

import java.time.Instant;

/**
 * Lớp cơ sở trừu tượng cho tất cả các Domain Event trong Payment Service.
 * T: là kiểu dữ liệu của "payload" (ví dụ: Payment).
 */
public abstract class PaymentDomainEvent<T> {
    private final T payload;
    private final Instant createdAt;

    public PaymentDomainEvent(T payload) {
        this.payload = payload;
        this.createdAt = Instant.now();
    }

    public T getPayload() {
        return payload;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public abstract String getEventType();
}