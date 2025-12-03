package com.example.payment.service;

import com.example.payment.entity.Payment;
import com.example.payment.event.*;
import com.example.payment.exception.PaymentDomainException;
import com.example.payment.valueobject.PaymentStatus;

/**
 * Triển khai Domain Service cho việc xử lý thanh toán.
 */
public class PaymentDomainServiceImpl implements PaymentDomainService {

    @Override
    public PaymentCompletedEvent validateAndInitializePayment(Payment payment) {
        // 1. Khởi tạo Payment (Gán ID, PENDING, v.v.)
        payment.initializePayment();

        // 2. Xác thực quy tắc nghiệp vụ nội bộ (giá cả, trạng thái...)
        payment.complete();

        // 3. Trả về sự kiện khi thanh toán được tạo thành công
        return new PaymentCompletedEvent(payment);
    }

    @Override
    public PaymentRefundedEvent refundPayment(Payment payment) {
        // 1. Xác thực trạng thái trước khi hoàn tiền
        if (payment.getPaymentStatus() != PaymentStatus.COMPLETED) {
            throw new PaymentDomainException("Payment không thể hoàn tiền ở trạng thái hiện tại: " + payment.getPaymentStatus());
        }

        // 2. Thực hiện logic hoàn tiền
        payment.refund();

        // 3. Trả về sự kiện hoàn tiền
        return new PaymentRefundedEvent(payment);
    }

    @Override
    public PaymentFailedEvent compensatePayment(Payment payment, String failureReason) {
        // 1. Thực hiện bù trừ (ví dụ: fail hoặc refund tùy ngữ cảnh SAGA)
        payment.fail(failureReason);

        // 2. Trả về sự kiện bù trừ
        return new PaymentFailedEvent(payment, failureReason);
    }
}