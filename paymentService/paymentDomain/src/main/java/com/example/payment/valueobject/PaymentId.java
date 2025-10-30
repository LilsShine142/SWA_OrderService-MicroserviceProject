package com.example.payment.valueobject;

import java.util.UUID;

/**
 * Value Object đại diện cho ID của Payment
 */
public class PaymentId {

    private final UUID value;

    /**
     * Constructor
     * @param value Giá trị UUID của Payment ID
     */
    public PaymentId(UUID value) {
        this.value = value != null ? value : UUID.randomUUID(); // Mặc định tạo UUID nếu null
    }

    /**
     * Trả về giá trị UUID
     */
    public UUID getValue() {
        return value;
    }

    /**
     * Tạo PaymentId mới với UUID ngẫu nhiên
     */
    public static PaymentId generate() {
        return new PaymentId(UUID.randomUUID());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentId paymentId = (PaymentId) o;
        return value.equals(paymentId.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}