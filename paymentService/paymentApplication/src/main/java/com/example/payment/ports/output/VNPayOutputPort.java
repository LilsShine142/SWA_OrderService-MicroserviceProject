package com.example.payment.ports.output;

import com.example.payment.dto.CreatePaymentCommand;
import com.example.payment.entity.Payment;
import java.util.Map;

public interface VNPayOutputPort {
    String generatePaymentUrl(Payment payment, CreatePaymentCommand command, String vnpTxnRef, Map<String, String> paymentCache);
    /**
     * Verify checksum for VNPay callback
     */
    boolean verifyChecksum(Map<String, String> params, String originalHashData);
}