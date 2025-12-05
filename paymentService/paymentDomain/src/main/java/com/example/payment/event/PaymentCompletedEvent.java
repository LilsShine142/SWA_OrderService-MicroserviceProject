package com.example.payment.event;

import com.example.payment.entity.Payment;
/**
 * Sự kiện được bắn ra khi một thanh toán được hoàn thành thành công.
 */
public class PaymentCompletedEvent extends PaymentDomainEvent<Payment> {
     public PaymentCompletedEvent(Payment payment) {
        super(payment);
    }

    @Override
    public String getEventType() {
        return "";
    }
}
