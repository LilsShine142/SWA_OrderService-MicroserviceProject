package com.example.payment.event;

import com.example.payment.entity.Payment;

/**
 * Sự kiện được bắn ra khi một thanh toán bị hủy.
 */
public class PaymentCancelledEvent extends PaymentDomainEvent<Payment> {

    /**
     * Hàm khởi tạo này nhận Payment (payload)
     * @param payment Đối tượng Payment đại diện cho sự kiện
     */
    public PaymentCancelledEvent(Payment payment) {
        super(payment);
    }

    @Override
    public String getEventType() {
        return "";
    }

    @Override
    public String toString() {
        return "PaymentCancelledEvent{" +
                "payload=" + getPayload() +
                ", createdAt=" + getCreatedAt() +
                '}';
    }
}