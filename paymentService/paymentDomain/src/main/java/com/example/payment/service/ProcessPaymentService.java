package com.example.payment.service;


import com.example.payment.entity.Payment;
import com.example.payment.event.CompensationEvent;
import com.example.payment.event.PaymentCreatedEvent;
import com.example.payment.event.PaymentDomainEvent;
import com.example.payment.event.PaymentRefundedEvent;

/**
 * Domain Service Interface (Lõi nghiệp vụ).
 * Định nghĩa các hành động cần thiết cho việc xử lý thanh toán.
 */
interface ProcessPaymentService {

    /**
     * Xác thực (validate) Payment, khởi tạo các trường giá trị (initialize),
     * và tạo ra sự kiện PaymentDomainEvent.
     * @param payment Payment Aggregate cần xử lý
     * @return PaymentDomainEvent (Sự kiện đã tạo)
     */
    PaymentCreatedEvent validateAndInitializePayment(Payment payment);

    /**
     * Xử lý logic hoàn tiền cho Payment.
     * @param payment Payment Aggregate cần hoàn tiền
     * @return PaymentDomainEvent (Sự kiện hoàn tiền)
     */
    PaymentRefundedEvent refundPaymentLogic(Payment payment);

    /**
     * Xử lý logic bù trừ (compensation) cho SAGA.
     * @param payment Payment Aggregate cần bù trừ
     * @return PaymentDomainEvent (Sự kiện bù trừ)
     */
    CompensationEvent compensatePaymentLogic(Payment payment);
}