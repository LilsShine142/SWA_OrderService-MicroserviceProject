package com.example.payment.ports.input.service;

import com.example.payment.dto.*;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * Đây là Input Port cho Payment Application Service
 * Định nghĩa các use case mà Payment Service cung cấp
 */
public interface PaymentApplicationService {

    /**
     * Tạo một giao dịch thanh toán mới và generate VNPay URL
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
     * Lấy danh sách tất cả payments
     */
    List<TrackPaymentResponse> getAllPayments();

    /**
     * Xử lý VNPay callback/IPN (sandbox)
     */
    ResponseData handleVnpayCallback(Map<String, String> params);

    /**
     * Refund Payment (sandbox, SAGA compensation)
     */
    ResponseData refundPayment(RefundPaymentCommand command) throws UnsupportedEncodingException;
}