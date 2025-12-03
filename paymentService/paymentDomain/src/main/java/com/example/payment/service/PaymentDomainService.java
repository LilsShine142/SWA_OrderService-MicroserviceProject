package com.example.payment.service;


import com.example.payment.entity.Payment;
import com.example.payment.event.*;

/**
 * Domain Service Interface (Lõi nghiệp vụ).
 * Định nghĩa các hành động cần thiết cho việc xử lý thanh toán.
 */
public interface PaymentDomainService {

    /**
     * Xác thực (validate) Payment, khởi tạo các trường giá trị (initialize),
     * và tạo ra sự kiện PaymentDomainEvent.
     * @param payment Payment Aggregate cần xử lý
     * @return PaymentCompletedEvent (Sự kiện đã tạo)
     */
    PaymentCompletedEvent validateAndInitializePayment(Payment payment);

    /**
     * Xử lý logic hoàn tiền cho Payment.
     * @param payment Payment Aggregate cần hoàn tiền
     * @return PaymentRefundedEvent (Sự kiện hoàn tiền)
     */
    PaymentRefundedEvent refundPayment(Payment payment);

    /**
     * Xử lý logic bù trừ (compensation) cho SAGA (thường là refund trong choreography).
     * @param payment Payment Aggregate cần bù trừ
     * @return PaymentFailedEvent (Sự kiện bù trừ, ví dụ fail hoặc refund)
     */
    PaymentFailedEvent compensatePayment(Payment payment, String failureReason);
}