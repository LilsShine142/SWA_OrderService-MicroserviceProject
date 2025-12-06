package com.example.payment.config;

import com.example.payment.dto.CreatePaymentCommand;
import com.example.payment.entity.Payment;
import com.example.payment.ports.output.VNPayOutputPort;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

@Component
public class VNPayAdapter implements VNPayOutputPort {

    @Override
    public String generatePaymentUrl(Payment payment, CreatePaymentCommand request, String vnpTxnRef, String vnpPayUrl, String vnpTmnCode, String vnpHashSecret, String vnpReturnUrl, Map<String, String> paymentCache) {
        Map<String, String> vnp_Params = new TreeMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", vnpTmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(request.getAmount().multiply(new BigDecimal(100)).longValue()));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnpTxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + request.getOrderId());
        vnp_Params.put("vnp_OrderType", "billpayment");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnpReturnUrl);
        vnp_Params.put("vnp_IpAddr", "127.0.0.1");

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        StringBuilder hashData = new StringBuilder();
        for (Map.Entry<String, String> param : vnp_Params.entrySet()) {
            if (hashData.length() > 0) {
                hashData.append('&');
            }
            try {
                hashData.append(param.getKey()).append('=').append(URLEncoder.encode(param.getValue(), StandardCharsets.UTF_8.toString()));
            } catch (Exception e) {
                // Handle
            }
        }

        String vnp_SecureHash = new HmacUtils(HmacAlgorithms.HMAC_SHA_512, vnpHashSecret).hmacHex(hashData.toString());
        vnp_Params.put("vnp_SecureHash", vnp_SecureHash);

        paymentCache.put(vnpTxnRef, hashData.toString()); // Cache hashData

        StringBuilder paymentUrl = new StringBuilder(vnpPayUrl + "?");
        for (Map.Entry<String, String> param : vnp_Params.entrySet()) {
            if (paymentUrl.length() > vnpPayUrl.length() + 1) {
                paymentUrl.append('&');
            }
            try {
                paymentUrl.append(param.getKey()).append('=').append(URLEncoder.encode(param.getValue(), StandardCharsets.UTF_8.toString()));
            } catch (Exception e) {
                // Handle
            }
        }

        return paymentUrl.toString();
    }

    @Override
    public boolean verifyChecksum(Map<String, String> params, String originalHashData, String vnpHashSecret) {
        // Build hashData from params, excluding vnp_SecureHash
        Map<String, String> sortedParams = new TreeMap<>(params);
        sortedParams.remove("vnp_SecureHash");

        StringBuilder hashData = new StringBuilder();
        for (Map.Entry<String, String> param : sortedParams.entrySet()) {
            if (hashData.length() > 0) {
                hashData.append('&');
            }
            try {
                hashData.append(param.getKey()).append('=').append(URLEncoder.encode(param.getValue(), StandardCharsets.UTF_8.toString()));
            } catch (Exception e) {
                // Handle
            }
        }

        String calculatedHash = new HmacUtils(HmacAlgorithms.HMAC_SHA_512, vnpHashSecret).hmacHex(hashData.toString());
        return calculatedHash.equals(params.get("vnp_SecureHash"));
    }

    @Override
    public void requestRefund(Payment payment, String transactionNo, String reason, String vnpRefundUrl, String vnpTmnCode, String vnpHashSecret) {
        // Implement refund logic as in sample, using HTTP post to vnpRefundUrl
        // Similar to sample's refundPayment
        Map<String, String> params = new TreeMap<>();
        // Fill params as in sample
        // Calculate hash
        // Send POST request
        // Handle response
    }
}
