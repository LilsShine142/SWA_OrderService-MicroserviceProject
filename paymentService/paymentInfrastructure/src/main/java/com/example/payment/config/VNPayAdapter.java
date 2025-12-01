package com.example.payment.config;

import com.example.payment.dto.CreatePaymentCommand;
import com.example.payment.entity.Payment;
import com.example.payment.ports.output.VNPayOutputPort;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class VNPayAdapter implements VNPayOutputPort {

    @Override
    public String generatePaymentUrl(Payment payment, CreatePaymentCommand command, String vnpTxnRef, Map<String, String> paymentCache) {
        // Stub implementation for demo
        return "https://sandbox.vnpayment.vn/payment?txnRef=" + vnpTxnRef + "&amount=" + payment.getAmount();
    }

    @Override
    public boolean verifyChecksum(Map<String, String> params, String originalHashData) {
        // Stub implementation for demo
        return true;
    }
}
