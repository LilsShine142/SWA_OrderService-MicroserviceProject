package com.example.payment.ports.output;

import com.example.payment.entity.Payment;
import com.example.payment.valueobject.PaymentId;

import java.util.Optional;

/**
 * Output Port - Repository interface trong Domain layer
 * Implementation nằm ở Infrastructure layer (PaymentRepositoryImpl)
 */
public interface PaymentRepository {

    Payment save(Payment payment);

    Optional<Payment> findById(PaymentId paymentId);
}