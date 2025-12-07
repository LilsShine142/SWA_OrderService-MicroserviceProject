package com.example.payment.ports.output;

import com.example.payment.entity.Payment;
import com.example.payment.valueobject.PaymentId;
import com.example.payment.valueobject.OrderId;

import java.util.List;
import java.util.Optional;

/**
 * Output Port - Repository interface trong Domain layer
 * Implementation nằm ở Infrastructure layer (PaymentRepositoryImpl)
 */
public interface PaymentRepository {

    Payment save(Payment payment);

    Optional<Payment> findById(PaymentId paymentId);

    List<Payment> findAll();

    Optional<Payment> findByOrderId(OrderId orderId);
}