package com.example.payment.event;

import com.example.payment.entity.Payment;

/**
 * Sự kiện được bắn ra khi một thanh toán được bù trừ (compensation) trong SAGA.
 */
public class CompensationEvent extends PaymentDomainEvent<Payment> {

    /**
     * Hàm khởi tạo này nhận Payment (payload) đại diện cho sự kiện bù trừ.
     * @param payment Đối tượng Payment liên quan đến sự kiện
     */
    public CompensationEvent(Payment payment) {
        super(payment);
    }

    @Override
    public String getEventType() {
        return "Compensation";
    }

    @Override
    public String toString() {
        return "CompensationEvent{" +
                "payload=" + getPayload() +
                ", createdAt=" + getCreatedAt() +
                '}';
    }
}
