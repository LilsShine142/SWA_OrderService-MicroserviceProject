package com.example.payment.ports.output;

import com.example.payment.entity.Payment;
import com.example.payment.event.PaymentDomainEvent;

public interface MessagePaymentEventPublisher {
    void publish(PaymentDomainEvent<Payment> event);
}