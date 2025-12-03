package com.example.payment.event;

import com.example.payment.entity.Payment;
import lombok.Getter;

import java.time.Instant;

/**
 * Lớp cơ sở trừu tượng cho tất cả các Domain Event trong Payment Service.
 * T: là kiểu dữ liệu của "payload" (ví dụ: Payment).
 */
@Getter
public abstract class PaymentDomainEvent<T> {
    private final T payload;
    private final Instant createdAt;

    public PaymentDomainEvent(T payload) {
        this.payload = payload;
        this.createdAt = Instant.now();
    }

    public abstract String getEventType();
}