package com.example.payment.ports.output;

//import com.example.payment.event.PaymentCompletedEvent;
//import com.example.payment.dto.PaymentFailedEvent;
import com.example.common_messaging.dto.event.PaymentCompletedEvent;
import com.example.common_messaging.dto.event.PaymentFailedEvent;
import com.example.payment.entity.Payment;
import com.example.payment.event.PaymentDomainEvent;

public interface PaymentCompletedEventPublisher {
    void publish(PaymentCompletedEvent event);
}