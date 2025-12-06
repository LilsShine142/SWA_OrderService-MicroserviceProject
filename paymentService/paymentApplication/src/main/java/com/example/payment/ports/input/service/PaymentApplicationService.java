package com.example.payment.ports.input.service;

import com.example.payment.dto.*;

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
     *
     * @return
     */
    ResponseData handleCallback(Map<String, String> params);

    /**
     * Refund Payment (sandbox, SAGA compensation)
     */
    void refundPayment(UUID paymentId, String transactionNo, String reason);

    void processPaymentFromEvent(OrderEvent event);

    /**
     * Method để simulate set order status (chỉ dùng cho test)
     */
    void setOrderStatusForSimulation(UUID orderId, String status);
}