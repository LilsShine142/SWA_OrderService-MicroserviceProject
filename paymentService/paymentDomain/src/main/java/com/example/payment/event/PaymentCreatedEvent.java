package com.example.payment.event;

import com.example.payment.entity.Payment;

/**
 * Sự kiện được bắn ra khi một thanh toán được tạo thành công.
 */
public class PaymentCreatedEvent extends PaymentDomainEvent<Payment> {

    /**
     * Hàm khởi tạo này nhận Payment (payload)
     * @param payment Đối tượng Payment đại diện cho sự kiện
     */
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