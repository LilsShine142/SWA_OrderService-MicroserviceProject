package com.example.payment.service;

import com.example.payment.entity.Payment;
import com.example.payment.event.CompensationEvent;
import com.example.payment.event.PaymentCreatedEvent;
import com.example.payment.event.PaymentRefundedEvent;
import com.example.payment.exception.PaymentDomainException;

/**
 * Triển khai Domain Service cho việc xử lý thanh toán.
 */
public class ProcessPaymentServiceImpl implements ProcessPaymentService {

    @Override
    public PaymentCreatedEvent validateAndInitializePayment(Payment payment) {

        // 1. Khởi tạo Payment (Gán ID, PENDING, v.v.)
        payment.initializePayment();

        // 2. Xác thực quy tắc nghiệp vụ nội bộ (giá cả, trạng thái...)
        payment.validatePayment();

        // 3. Trả về sự kiện khi thanh toán được tạo thành công
        return (PaymentCreatedEvent) payment.getEvents().stream()
                .filter(event -> event instanceof PaymentCreatedEvent)
                .findFirst()
                .map(event -> (PaymentCreatedEvent) event)
                .orElseThrow(() -> new PaymentDomainException("Không tìm thấy PaymentCreatedEvent sau khi khởi tạo."));
    }

    @Override
    public PaymentRefundedEvent refundPaymentLogic(Payment payment) {

        // 1. Xác thực trạng thái trước khi hoàn tiền
        if (!payment.canRefund()) {
            throw new PaymentDomainException("Payment không thể hoàn tiền ở trạng thái hiện tại: " + payment.getPaymentStatus());
        }

        // 2. Thực hiện logic hoàn tiền
        payment.performRefund();

        // 3. Trả về sự kiện hoàn tiền
        return (PaymentRefundedEvent) payment.getEvents().stream()
                .filter(event -> event instanceof PaymentRefundedEvent)
                .reduce((first, second) -> second) // Lấy sự kiện cuối cùng
                .map(event -> (PaymentRefundedEvent) event)
                .orElseThrow(() -> new PaymentDomainException("Không tìm thấy PaymentRefundedEvent sau khi hoàn tiền."));
    }

    /**
     * Xử lý logic bù trừ (compensation) cho SAGA.
     * @param payment Payment cần bù trừ
     * @return CompensationEvent sự kiện bù trừ
     */
    public CompensationEvent compensatePaymentLogic(Payment payment) {

        // 1. Thực hiện bù trừ
        payment.performCompensation();

        // 2. Trả về sự kiện bù trừ
        return (CompensationEvent) payment.getEvents().stream()
                .filter(event -> event instanceof CompensationEvent)
                .reduce((first, second) -> second) // Lấy sự kiện cuối cùng
                .map(event -> (CompensationEvent) event)
                .orElseThrow(() -> new PaymentDomainException("Không tìm thấy CompensationEvent sau khi bù trừ."));
    }
}