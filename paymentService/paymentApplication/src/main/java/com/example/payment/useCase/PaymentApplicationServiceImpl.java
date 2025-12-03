package com.example.payment.useCase;

import com.example.payment.dto.CreatePaymentCommand;
import com.example.payment.dto.OrderEvent;
import com.example.payment.dto.PaymentResponse;
import com.example.payment.entity.Payment;
import com.example.payment.event.PaymentCompletedEvent;
import com.example.payment.event.PaymentCreatedEvent;
import com.example.payment.event.PaymentFailedEvent;
import com.example.payment.event.PaymentRefundedEvent;
import com.example.payment.mapper.PaymentDataMapper;
import com.example.payment.ports.input.service.PaymentApplicationService;
import com.example.payment.ports.output.MessagePaymentEventPublisher;
import com.example.payment.ports.output.PaymentRepository;
import com.example.payment.ports.output.VNPayOutputPort;
import com.example.payment.valueobject.PaymentId;
import com.example.payment.valueobject.PaymentStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PaymentApplicationServiceImpl implements PaymentApplicationService {
    private static final Logger log = LoggerFactory.getLogger(PaymentApplicationServiceImpl.class);
    private final PaymentRepository paymentRepositoryPort;
    private final MessagePaymentEventPublisher messagePublisherPort;
    private final VNPayOutputPort vnPayOutputPort;
    private final PaymentDataMapper paymentDataMapper;
    private final Map<String, String> orderStatusCache = new ConcurrentHashMap<>();

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

    private final Map<String, String> paymentCache = new ConcurrentHashMap<>();

    public PaymentApplicationServiceImpl(PaymentRepository paymentRepositoryPort,
                                         MessagePaymentEventPublisher messagePublisherPort,
                                         VNPayOutputPort vnPayOutputPort,
                                         PaymentDataMapper paymentDataMapper) {
        this.paymentRepositoryPort = paymentRepositoryPort;
        this.messagePublisherPort = messagePublisherPort;
        this.vnPayOutputPort = vnPayOutputPort;
        this.paymentDataMapper = paymentDataMapper;
    }

//    @Override
//    @Transactional
//    public PaymentResponse processPayment(CreatePaymentCommand request) {
//        try {
//            Payment payment = paymentDataMapper.paymentRequestToPayment(request);
////        Tạm thời không check để test VNPay
////        payment.initializePayment();
////        payment.validatePayment();
//            payment.setId(new PaymentId(UUID.randomUUID()));
//            Payment savedPayment = paymentRepositoryPort.save(payment);
//            log.info("2. Created payment with ID: {}", savedPayment.getId());
//            String vnpTxnRef = savedPayment.getId().toString();
//            String paymentUrl = vnPayOutputPort.generatePaymentUrl(savedPayment, request, vnpTxnRef, vnpPayUrl, vnpTmnCode, vnpHashSecret, vnpReturnUrl, paymentCache);
//            log.info("3. Generated VNPay payment URL: {}", paymentUrl);
//
//            // Publish event for SAGA
//            messagePublisherPort.publish(new PaymentCreatedEvent(savedPayment)); // Initiation event
//
//            return paymentDataMapper.paymentToPaymentResponse(savedPayment, "Payment initiated successfully", paymentUrl);
//        } catch (Exception e) {
//            log.error("Error processing payment for orderId: {}", request.getOrderId(), e);
//            // Return error response
//            PaymentResponse errorResponse = new PaymentResponse();
//            errorResponse.setMessage("Payment processing failed: " + e.getMessage());
//            errorResponse.setStatus(PaymentStatus.FAILED);
//            return errorResponse;
//        }
//    }

    // --- 1. HÀM ĐƯỢC GỌI BỞI CONSUMER  ---
    @Override
    @Transactional
    public void processPaymentFromEvent(OrderEvent event) {
        log.info("📥 PaymentService nhận OrderEvent: orderId={}, status={}", event.getOrderId(), event.getStatus());

        // Cập nhật Cache
        orderStatusCache.put(event.getOrderId().toString(), event.getStatus());

        if ("APPROVED".equals(event.getStatus())) {
            log.info("Order approved -> Có thể kích hoạt logic thanh toán tự động tại đây nếu cần");
        }
    }

    @Override
    @Transactional
    public PaymentResponse processPayment(CreatePaymentCommand paymentRequest) {
        // Kiểm tra trạng thái order từ event
        String orderStatus = orderStatusCache.getOrDefault(paymentRequest.getOrderId().toString(), "UNKNOWN");
        if (!"APPROVED".equals(orderStatus)) {
            PaymentResponse errorResponse = new PaymentResponse();
            errorResponse.setMessage("Order is not approved for payment. Current status: " + orderStatus);
            errorResponse.setStatus(PaymentStatus.FAILED);
            return errorResponse;
        }

        // 1. Tạo Payment Entity & Gán ID
        Payment payment = paymentDataMapper.paymentRequestToPayment(paymentRequest);
        payment.initializePayment();

        try {
            // --- TRƯỜNG HỢP THÀNH CÔNG ---
            payment.setPaymentStatus(PaymentStatus.COMPLETED);
            paymentRepositoryPort.save(payment); // Cập nhật DB

            // Bắn Event: PAYMENT COMPLETED
            log.info("Thanh toán thành công. Đang gửi event PaymentCompleted...");
            messagePublisherPort.publish(new PaymentCompletedEvent(payment));

            PaymentResponse successResponse = new PaymentResponse();
            successResponse.setPaymentId(payment.getId().value());
            successResponse.setStatus(PaymentStatus.COMPLETED);
            successResponse.setMessage("Thanh toán thành công");
            return successResponse;

        } catch (Exception e) {
            // --- TRƯỜNG HỢP THẤT BẠI ---
            log.error("Thanh toán thất bại: {}", e.getMessage());

            payment.setPaymentStatus(PaymentStatus.FAILED);
            payment.setFailureReason(String.valueOf(List.of(e.getMessage())));
            paymentRepositoryPort.save(payment); // Cập nhật DB

            // Bắn Event: PAYMENT FAILED
            log.info("Đang gửi event PaymentFailed...");
            messagePublisherPort.publish(new PaymentFailedEvent(payment, String.valueOf(List.of(e.getMessage()))));

            PaymentResponse failedResponse = new PaymentResponse();
            failedResponse.setPaymentId(payment.getId().value());
            failedResponse.setStatus(PaymentStatus.FAILED);
            failedResponse.setMessage("Thanh toán thất bại: " + e.getMessage());
            return failedResponse;
        }
    }

    @Override
    @Transactional
    public void handleCallback(Map<String, String> params) {
        // Logic from sample: verify checksum, update payment, publish event
        String vnp_SecureHash = params.get("vnp_SecureHash");
        String billID = params.get("vnp_TxnRef");

        if (billID == null || vnp_SecureHash == null) {
            throw new RuntimeException("Missing billID or signature");
        }

        String originalHashData = paymentCache.get(billID);
        boolean isValid = vnPayOutputPort.verifyChecksum(params, originalHashData, vnpHashSecret);

        if (isValid) {
            paymentCache.remove(billID);
            String transactionStatus = params.get("vnp_TransactionStatus");
            UUID paymentId = UUID.fromString(billID);
            Payment payment = paymentRepositoryPort.findById(new PaymentId(paymentId))
                    .orElseThrow(() -> new RuntimeException("Payment not found"));

            if ("00".equals(transactionStatus)) {
                payment.complete();
                payment.setTransactionId(params.get("vnp_TransactionNo"));
                messagePublisherPort.publish(new PaymentCompletedEvent(payment));
            } else {
                payment.fail("VNPay response code: " + params.get("vnp_ResponseCode"));
                messagePublisherPort.publish(new PaymentFailedEvent(payment, "Failed"));
            }
            paymentRepositoryPort.save(payment);
        } else {
            // Fallback logic as in sample
            // ... (implement similar fallback)
        }
    }

    @Override
    @Transactional
    public void refundPayment(UUID paymentId, String transactionNo, String reason) {
        Payment payment = paymentRepositoryPort.findById(new PaymentId(paymentId))
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        vnPayOutputPort.requestRefund(payment, transactionNo, reason, vnpRefundUrl, vnpTmnCode, vnpHashSecret);

        payment.refund();
        paymentRepositoryPort.save(payment);
        messagePublisherPort.publish(new PaymentRefundedEvent(payment));
    }
}
