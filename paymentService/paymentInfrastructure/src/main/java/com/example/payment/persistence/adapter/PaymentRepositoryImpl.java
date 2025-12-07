package com.example.payment.persistence.adapter;

import com.example.payment.entity.Payment;
import com.example.payment.persistence.entity.PaymentEntity;
import com.example.payment.persistence.mapper.PaymentPersistenceDataMapper;
import com.example.payment.persistence.repository.PaymentJpaRepository;
import com.example.payment.ports.output.PaymentRepository;
import com.example.payment.valueobject.OrderId;
import com.example.payment.valueobject.PaymentId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * ADAPTER PATTERN
 * Triển khai Output Port 'PaymentRepository'
 * Vị trí: paymentInfrastructure/adapter/
 */
@Component // Báo cho Spring biết đây là Bean
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository paymentJpaRepository;
    private final PaymentPersistenceDataMapper paymentPersistenceDataMapper;

    // Constructor Injection
    public PaymentRepositoryImpl(PaymentJpaRepository paymentJpaRepository,
                                 PaymentPersistenceDataMapper paymentPersistenceDataMapper) {
        this.paymentJpaRepository = paymentJpaRepository;
        this.paymentPersistenceDataMapper = paymentPersistenceDataMapper;
    }

    @Override
    public Payment save(Payment payment) {
        // 1. Chuyển Domain -> JPA Entity
        PaymentEntity paymentEntity = paymentPersistenceDataMapper.paymentToPaymentEntity(payment);

        // 2. Lưu bằng Spring Data JPA
        PaymentEntity savedEntity = paymentJpaRepository.save(paymentEntity);

        // 3. Chuyển JPA Entity -> Domain
        return paymentPersistenceDataMapper.paymentEntityToPayment(savedEntity);
    }

    @Override
    public Optional<Payment> findById(PaymentId paymentId) {
        Optional <PaymentEntity> paymentEntityOptional = paymentJpaRepository.findById(paymentId.value());
        return paymentEntityOptional
                .map(paymentPersistenceDataMapper::paymentEntityToPayment);
    }

    @Override
    public Optional<Payment> findByOrderId(OrderId orderId) {
        Optional<PaymentEntity> paymentEntityOptional = paymentJpaRepository.findByOrderId(orderId.value());
        return paymentEntityOptional
                .map(paymentPersistenceDataMapper::paymentEntityToPayment);
    }

    @Override
    public List<Payment> findAll() {
        return List.of();
    }
}
