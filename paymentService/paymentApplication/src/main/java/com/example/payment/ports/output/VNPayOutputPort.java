package com.example.payment.ports.output;

import com.example.payment.dto.CreatePaymentCommand;
import com.example.payment.entity.Payment;
import java.util.Map;

public interface VNPayOutputPort {
    String generatePaymentUrl(Payment payment, CreatePaymentCommand request, String vnpTxnRef, String vnpPayUrl, String vnpTmnCode, String vnpHashSecret, String vnpReturnUrl, Map<String, String> paymentCache);
    boolean verifyChecksum(Map<String, String> params, String originalHashData, String vnpHashSecret);
    void requestRefund(Payment payment, String transactionNo, String reason, String vnpRefundUrl, String vnpTmnCode, String vnpHashSecret);
}