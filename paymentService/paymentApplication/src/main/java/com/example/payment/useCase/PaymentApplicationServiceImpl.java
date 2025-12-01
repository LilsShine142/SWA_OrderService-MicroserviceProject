package com.example.payment.useCase;

import com.example.payment.dto.CancelPaymentCommand;
import com.example.payment.dto.CancelPaymentResponse;
import com.example.payment.dto.CreatePaymentCommand;
import com.example.payment.dto.CreatePaymentResponse;
import com.example.payment.dto.RefundPaymentCommand;
import com.example.payment.dto.TrackPaymentQuery;
import com.example.payment.dto.TrackPaymentResponse;
import com.example.payment.entity.Payment;
import com.example.payment.exception.PaymentNotFoundException;
import com.example.payment.event.PaymentCreatedEvent;
import com.example.payment.event.PaymentCancelledEvent;
import com.example.payment.event.PaymentRefundedEvent;
import com.example.payment.exception.PaymentProcessingException;
import com.example.payment.mapper.PaymentDataMapper;
import com.example.payment.ports.input.service.PaymentApplicationService;
import com.example.payment.ports.output.MessageBrokerOutputPort;
import com.example.payment.ports.output.PaymentRepository;
import com.example.payment.ports.output.VNPayOutputPort;
import com.example.payment.valueobject.PaymentStatus;
import com.example.payment.dto.ResponseData;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PaymentApplicationServiceImpl implements PaymentApplicationService {
    private final PaymentRepository paymentRepository;
    private final MessageBrokerOutputPort messageBrokerOutputPort;
    private final VNPayOutputPort vnPayOutputPort;
    private final PaymentDataMapper paymentDataMapper;
    @Value("${vnp.pay.url}")
    private String vnpPayUrl;
    @Value("${vnp.tmnCode}")
    private String vnpTmnCode;
    @Value("${vnp.hashSecret}")
    private String vnpHashSecret;
    @Value("${vnp.returnUrl}")
    private String vnpReturnUrl;
    @Value("${vnp.refund.url}")
    private String vnpRefundUrl;
    // In-Memory Cache để lưu hashData gốc
    private final Map<String, String> paymentCache = new ConcurrentHashMap<>();

    public PaymentApplicationServiceImpl(PaymentRepository paymentRepository,
                                         MessageBrokerOutputPort messageBrokerOutputPort,
                                         VNPayOutputPort vnPayOutputPort,
                                         PaymentDataMapper paymentDataMapper) {
        this.paymentRepository = paymentRepository;
        this.messageBrokerOutputPort = messageBrokerOutputPort;
        this.vnPayOutputPort = vnPayOutputPort;
        this.paymentDataMapper = paymentDataMapper;
    }

    /**
     * Triển khai Use Case: Create Payment with VNPay URL
     * SAGA Step: PAYMENT_INITIATED
     */
    @Override
    @Transactional
    public CreatePaymentResponse createPayment(CreatePaymentCommand command) {
        Payment payment = paymentDataMapper.createPaymentCommandToPayment(command);
        payment.initializePayment();
        payment.setSagaStep("PAYMENT_INITIATED");
        Payment savedPayment = paymentRepository.save(payment);
        String vnpTxnRef = savedPayment.getId().toString();
        String paymentUrl = vnPayOutputPort.generatePaymentUrl(savedPayment, command, vnpTxnRef, paymentCache);
        PaymentCreatedEvent event = new PaymentCreatedEvent(savedPayment);
        messageBrokerOutputPort.sendMessage("payment-events", event);
        savedPayment.setSagaStep("PAYMENT_CREATED"); // SAGA: Payment URL generated
        paymentRepository.update(savedPayment);
        return paymentDataMapper.paymentToCreatePaymentResponse(savedPayment, "Payment created successfully", paymentUrl);
    }

    @Override
    @Transactional
    public CancelPaymentResponse cancelPayment(CancelPaymentCommand command) {
        Payment payment = paymentRepository.findById(command.paymentId())
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with id: " + command.paymentId()));
        if (payment.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new PaymentProcessingException("Cannot cancel payment with status: " + payment.getPaymentStatus());
        }
        payment.setPaymentStatus(PaymentStatus.FAILED);
        payment.setFailureReason(command.failureReason());
        payment.setSagaStep("PAYMENT_CANCELLED");
        payment.setUpdatedAt(Instant.now());
        Payment savedPayment = paymentRepository.update(payment);
        PaymentCancelledEvent event = new PaymentCancelledEvent(savedPayment);
        messageBrokerOutputPort.sendMessage("payment-events", event);
        return paymentDataMapper.paymentToCancelPaymentResponse(savedPayment, "Payment cancelled successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public TrackPaymentResponse trackPayment(TrackPaymentQuery query) {
        Payment payment = paymentRepository.findById(query.paymentId())
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with id: " + query.paymentId()));
        return paymentDataMapper.paymentToTrackPaymentResponse(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrackPaymentResponse> getAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        return payments.stream()
                .map(paymentDataMapper::paymentToTrackPaymentResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Xử lý VNPay callback/IPN (sandbox)
     */
    @Override
    @Transactional
    public ResponseData handleVnpayCallback(Map<String, String> params) {
        System.out.println("=== VNPAY CALLBACK PARAMS ===");
        params.forEach((key, value) -> System.out.println(key + ": " + value));

        String vnp_SecureHash = params.get("vnp_SecureHash");
        String billID = params.get("vnp_TxnRef");

        if (billID == null || vnp_SecureHash == null) {
            return createErrorResponse("Thiếu thông tin billID hoặc chữ ký");
        }

        // LẤY HASHDATA GỐC TỪ CACHE
        String originalHashData = paymentCache.get(billID);
        if (originalHashData == null) {
            System.out.println("Không tìm thấy hashData trong cache cho billID: " + billID);
            return createErrorResponse("Không tìm thấy thông tin thanh toán hoặc đã hết hạn");
        }

        System.out.println("Retrieved original hashData from cache: " + originalHashData);

        // SỬ DỤNG HASHDATA GỐC ĐỂ VERIFY CHỮ KÝ
        boolean isValid = vnPayOutputPort.verifyChecksum(params, originalHashData);

        if (isValid) {
            // XÓA KHỎI CACHE SAU KHI XÁC THỰC THÀNH CÔNG
            paymentCache.remove(billID);
            System.out.println("Removed billID from cache: " + billID);

            String transactionStatus = params.get("vnp_TransactionStatus");
            return updatePaymentAfterCallback(billID, transactionStatus, params);
        } else {
            // Fallback: thử verify với params nhận được
            System.out.println("Trying fallback verification with received params...");
            ResponseData fallbackResult = verifyWithReceivedParams(params);
            if (fallbackResult.getStatus() == 200) {
                paymentCache.remove(billID); // Xóa khỏi cache nếu fallback thành công
            }
            return fallbackResult;
        }
    }

    private ResponseData verifyWithReceivedParams(Map<String, String> params) {
        try {
            String vnp_SecureHash = params.get("vnp_SecureHash");

            // Tạo bản sao của params để tính toán chữ ký
            Map<String, String> signParams = new TreeMap<>();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String key = entry.getKey();
                if (key.startsWith("vnp_") &&
                        !key.equals("vnp_SecureHash") &&
                        !key.equals("vnp_SecureHashType")) {
                    signParams.put(key, entry.getValue() != null ? entry.getValue() : "");
                }
            }

            StringBuilder hashData = new StringBuilder();
            for (Map.Entry<String, String> entry : signParams.entrySet()) {
                if (hashData.length() > 0) {
                    hashData.append('&');
                }
                hashData.append(entry.getKey()).append('=').append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            }

            String calculatedSignValue = new HmacUtils(HmacAlgorithms.HMAC_SHA_512, vnpHashSecret).hmacHex(hashData.toString());

            System.out.println("=== FALLBACK VERIFICATION ===");
            System.out.println("Fallback HashData: " + hashData.toString());
            System.out.println("Fallback Calculated Signature: " + calculatedSignValue);
            System.out.println("Fallback Received Signature: " + vnp_SecureHash);

            if (calculatedSignValue.equals(vnp_SecureHash)) {
                String transactionStatus = params.get("vnp_TransactionStatus");
                String billID = params.get("vnp_TxnRef");
                return updatePaymentAfterCallback(billID, transactionStatus, params);
            } else {
                ResponseData response = new ResponseData();
                response.setStatus(400);
                response.setMessage("Chữ ký không hợp lệ (fallback cũng thất bại)");
                // Debug info
                Map<String, String> debugInfo = new HashMap<>();
                debugInfo.put("fallbackHashData", hashData.toString());
                debugInfo.put("fallbackCalculated", calculatedSignValue);
                debugInfo.put("receivedSignature", vnp_SecureHash);
                response.setData(debugInfo);
                return response;
            }
        } catch (Exception e) {
            return createErrorResponse("Lỗi fallback verification: " + e.getMessage());
        }
    }

    private ResponseData updatePaymentAfterCallback(String billID, String transactionStatus, Map<String, String> params) {
        try {
            UUID paymentId = UUID.fromString(billID);
            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));

            ResponseData response = new ResponseData();

            if ("00".equals(transactionStatus)) {
                // Thanh toán thành công
                payment.setPaymentStatus(PaymentStatus.COMPLETED);
                payment.setTransactionId(params.get("vnp_TransactionNo"));
                payment.setTransactionEndAt(LocalDateTime.now());
                payment.setSagaStep("PAYMENT_COMPLETED"); // SAGA: Payment completed successfully
                messageBrokerOutputPort.sendMessage("payment-succeeded", payment.getId().toString());

                response.setStatus(200);
                response.setMessage("Thanh toán thành công");
                response.setData(payment);
            } else {
                // Thanh toán thất bại
                payment.setPaymentStatus(PaymentStatus.FAILED);
                payment.setFailureReason("VNPay response code: " + params.get("vnp_ResponseCode"));
                payment.setSagaStep("PAYMENT_FAILED"); // SAGA: Payment failed
                messageBrokerOutputPort.sendMessage("payment-failed", payment.getId().toString());

                response.setStatus(400);
                response.setMessage("Thanh toán thất bại: " + params.get("vnp_ResponseCode"));
                response.setData(payment);
            }

            payment.setUpdatedAt(Instant.now());
            paymentRepository.update(payment);
            return response;
        } catch (Exception e) {
            return createErrorResponse("Lỗi khi cập nhật payment: " + e.getMessage());
        }
    }

    /**
     * Triển khai Use Case: Refund Payment (SAGA Compensation)
     */
    @Override
    @Transactional
    public ResponseData refundPayment(RefundPaymentCommand command) throws UnsupportedEncodingException {
        Payment payment = paymentRepository.findById(command.getPaymentId())
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));

        if (payment.getPaymentStatus() != PaymentStatus.COMPLETED) {
            return createErrorResponse("Cannot refund non-completed payment");
        }

        // KIỂM TRA bắt buộc phải có transactionNo
        if (command.getTransactionNo() == null || command.getTransactionNo().isEmpty()) {
            return createErrorResponse("TransactionNo là bắt buộc để hoàn tiền");
        }

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        String vnp_TransactionDate = formatter.format(cld.getTime()); // Adjust to actual

        Map<String, String> params = new TreeMap<>();
        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "refund");
        params.put("vnp_TmnCode", vnpTmnCode);
        params.put("vnp_TransactionType", command.getTransactionType());
        params.put("vnp_TxnRef", command.getPaymentId().toString());
        params.put("vnp_Amount", String.valueOf(command.getAmount() * 100));
        params.put("vnp_OrderInfo", command.getReason());
        params.put("vnp_TransactionNo", command.getTransactionNo()); // BẮT BUỘC
        params.put("vnp_TransactionDate", vnp_TransactionDate);
        params.put("vnp_CreateDate", vnp_CreateDate);
        params.put("vnp_CreateBy", "system");
        params.put("vnp_IpAddr", "127.0.0.1");
        params.put("vnp_RequestId", UUID.randomUUID().toString());

        // Tạo chữ ký
        StringBuilder hashData = new StringBuilder();
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);
        for (String fieldName : fieldNames) {
            String fieldValue = params.get(fieldName);
            if (fieldValue != null && fieldValue.length() > 0) {
                if (hashData.length() > 0) {
                    hashData.append('&');
                }
                hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
            }
        }
        String vnp_SecureHash = new HmacUtils(HmacAlgorithms.HMAC_SHA_512, vnpHashSecret).hmacHex(hashData.toString());
        params.put("vnp_SecureHash", vnp_SecureHash);

        // GỬI REQUEST dưới dạng JSON
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(vnpRefundUrl);
        post.setHeader("Content-Type", "application/json");

        // Chuyển params sang JSON
        StringBuilder jsonBody = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!first) jsonBody.append(",");
            jsonBody.append("\"").append(entry.getKey()).append("\":\"").append(entry.getValue()).append("\"");
            first = false;
        }
        jsonBody.append("}");

        post.setEntity(new org.apache.http.entity.StringEntity(jsonBody.toString(), StandardCharsets.UTF_8));

        ResponseData response = new ResponseData();
        try (CloseableHttpResponse httpResponse = client.execute(post);
             BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8))) {

            StringBuilder responseBuilder = new StringBuilder();
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                responseBuilder.append(inputLine);
            }

            System.out.println("=== REFUND RESPONSE ===");
            System.out.println(responseBuilder.toString());

            // Parse JSON response
            Map<String, String> responseMap = parseJsonResponse(responseBuilder.toString());
            String rspCode = responseMap.get("vnp_ResponseCode");

            if (rspCode == null) {
                System.err.println("Refund failed: vnp_ResponseCode is null. Response: " + responseBuilder.toString());
                response.setStatus(400);
                response.setMessage("Hoàn tiền thất bại: Phản hồi từ VnPay không hợp lệ");
                response.setData(responseMap);
                return response;
            }

            if ("00".equals(rspCode)) {
                payment.refundPayment();
                payment.setSagaStep("PAYMENT_REFUNDED"); // SAGA: Compensation - Payment refunded
                payment.setUpdatedAt(Instant.now());
                paymentRepository.update(payment);
                PaymentRefundedEvent event = new PaymentRefundedEvent(payment);
                messageBrokerOutputPort.sendMessage("payment-events", event);
                response.setStatus(200);
                response.setMessage("Hoàn tiền thành công");
                response.setData(responseMap);
            } else {
                System.err.println("Refund failed with response code: " + rspCode);
                response.setStatus(400);
                response.setMessage("Hoàn tiền thất bại: " + getVnPayErrorMessage(rspCode));
                response.setData(responseMap);
            }
        } catch (Exception e) {
            System.err.println("Error during refund request: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(500);
            response.setMessage("Hoàn tiền thất bại do lỗi hệ thống: " + e.getMessage());
            response.setData(null);
        }

        return response;
    }

    private ResponseData createErrorResponse(String message) {
        ResponseData response = new ResponseData();
        response.setStatus(400);
        response.setMessage(message);
        return response;
    }

    private Map<String, String> parseJsonResponse(String jsonStr) {
        Map<String, String> map = new HashMap<>();
        try {
            jsonStr = jsonStr.trim();
            if (jsonStr.startsWith("{") && jsonStr.endsWith("}")) {
                jsonStr = jsonStr.substring(1, jsonStr.length() - 1);
                for (String pair : jsonStr.split(",")) {
                    String[] keyValue = pair.split(":", 2);
                    if (keyValue.length == 2) {
                        String key = keyValue[0].trim().replace("\"", "");
                        String value = keyValue[1].trim().replace("\"", "");
                        map.put(key, value);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
        }
        return map;
    }

    private String getVnPayErrorMessage(String rspCode) {
        switch (rspCode) {
            case "91": return "Giao dịch không tồn tại";
            case "94": return "Giao dịch đã được hoàn tiền trước đó";
            case "99": return "Lỗi không xác định từ VnPay";
            default: return "Mã lỗi VnPay: " + rspCode;
        }
    }
}

