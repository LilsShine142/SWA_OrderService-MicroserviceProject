package com.example.order.domain.core.event;



import java.time.Instant;

/**
 * Lớp cơ sở cho tất cả các Domain Event.
 * T: là kiểu dữ liệu của "payload" (ví dụ: Order)
 */
public abstract class DomainEvent<T> {
    private final T payload;
    private final Instant createdAt;

    public DomainEvent(T payload) {
        this.payload = payload;
        this.createdAt = Instant.now();
    }

    public T getPayload() {
        return payload;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}