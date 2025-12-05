package com.example.payment.ports.input.service;

import com.example.payment.dto.CreatePaymentCommand;
import com.example.payment.dto.OrderEvent;
import com.example.payment.dto.PaymentResponse;

import java.util.Map;
import java.util.UUID;

/**
 * Đây là Input Port cho Payment Application Service
 * Định nghĩa các use case mà Payment Service cung cấp
 */
public interface PaymentApplicationService {

    /**
     * Tạo một giao dịch thanh toán mới và generate VNPay URL
     */
    PaymentResponse processPayment(CreatePaymentCommand request);

    /**
     * Xử lý VNPay callback/IPN (sandbox)
     */
    void handleCallback(Map<String, String> params);

    /**
     * Refund Payment (sandbox, SAGA compensation)
     */
    void refundPayment(UUID paymentId, String transactionNo, String reason);

    void processPaymentFromEvent(OrderEvent event);
}