package com.example.payment.ports.input.service;

import com.example.payment.dto.*;

import java.util.List;
import java.util.UUID;

/**
 * Đây là Input Port cho Payment Application Service
 * Định nghĩa các use case mà Payment Service cung cấp
 */
public interface PaymentApplicationService {

    /**
     * Tạo một giao dịch thanh toán mới
     */
    CreatePaymentResponse createPayment(CreatePaymentCommand command);

    /**
     * Hủy một giao dịch thanh toán
     */
    CancelPaymentResponse cancelPayment(CancelPaymentCommand command);

    /**
     * Theo dõi trạng thái của một giao dịch thanh toán
     */
    TrackPaymentResponse trackPayment(TrackPaymentQuery query);

    /**
     * Lấy tất cả payments theo order ID
     */
    List<PaymentResponse> getPaymentsByOrder(UUID orderId);
}