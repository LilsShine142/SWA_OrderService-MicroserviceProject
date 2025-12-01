package com.example.payment.persistence.adapter;

import com.example.payment.entity.Payment;
import com.example.payment.persistence.entity.PaymentEntity;
import com.example.payment.persistence.mapper.PaymentPersistenceDataMapper;
import com.example.payment.persistence.repository.PaymentJpaRepository;
import com.example.payment.ports.output.PaymentRepository;
import com.example.payment.valueobject.OrderId;
import com.example.payment.valueobject.PaymentId;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    public Optional<Payment> findById(@NotNull UUID paymentId) {
        return paymentJpaRepository
                .findById(paymentId) // paymentId.getValue() là UUID
                .map    (paymentPersistenceDataMapper::paymentEntityToPayment);
    }

    @Override
    public void deleteById(PaymentId paymentId) {
        paymentJpaRepository.deleteById(paymentId.value());
    }

    @Override
    public Payment update(Payment payment) {
        // 1. Chuyển Domain -> JPA Entity
        PaymentEntity paymentEntity = paymentPersistenceDataMapper.paymentToPaymentEntity(payment);

        // 2. Lưu bằng Spring Data JPA (save sẽ update nếu entity có ID)
        PaymentEntity updatedEntity = paymentJpaRepository.save(paymentEntity);

        // 3. Chuyển JPA Entity -> Domain
        return paymentPersistenceDataMapper.paymentEntityToPayment(updatedEntity);
    }

    @Override
    public Optional<Payment> findByOrderId(OrderId orderId) {
        return paymentJpaRepository
                .findByOrderId(orderId.value())
                .map(paymentPersistenceDataMapper::paymentEntityToPayment);
    }

    @Override
    public List<Payment> findBySagaId(String sagaId) {
        return paymentJpaRepository
                .findBySagaId(sagaId)
                .stream()
                .map(paymentPersistenceDataMapper::paymentEntityToPayment)
                .toList();
    }
}
