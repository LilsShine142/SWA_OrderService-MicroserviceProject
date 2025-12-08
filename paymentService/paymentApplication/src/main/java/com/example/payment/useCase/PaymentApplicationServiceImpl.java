package com.example.payment.useCase;

import com.example.common_messaging.dto.event.PaymentFailedEvent;
import com.example.payment.dto.*;
import com.example.payment.entity.Payment;

import com.example.common_messaging.dto.event.OrderCreatedEvent;
//import com.example.common_messaging.dto.event.PaymentCompletedEvent;
import com.example.common_messaging.dto.event.PaymentCompletedEvent;
import com.example.payment.event.PaymentRefundedEvent;
import com.example.payment.mapper.PaymentDataMapper;
import com.example.payment.ports.input.service.PaymentApplicationService;
import com.example.payment.ports.output.MessagePaymentEventPublisher;
import com.example.payment.ports.output.PaymentCompletedEventPublisher;
import com.example.payment.ports.output.PaymentRepository;
import com.example.payment.ports.output.VNPayOutputPort;
import com.example.payment.valueobject.CustomerId;
import com.example.payment.valueobject.OrderId;
import com.example.payment.valueobject.PaymentId;
import com.example.payment.valueobject.PaymentStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class PaymentApplicationServiceImpl implements PaymentApplicationService {
    private static final Logger log = LoggerFactory.getLogger(PaymentApplicationServiceImpl.class);
    private final PaymentRepository paymentRepositoryPort;
    private final MessagePaymentEventPublisher messagePublisherPort;
    private final PaymentCompletedEventPublisher paymentCompletedEventPublisher;
    private final VNPayOutputPort vnPayOutputPort;
    private final PaymentDataMapper paymentDataMapper;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

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
                                         MessagePaymentEventPublisher messagePublisherPort, PaymentCompletedEventPublisher paymentCompletedEventPublisher,
                                         VNPayOutputPort vnPayOutputPort,
                                         PaymentDataMapper paymentDataMapper, StringRedisTemplate redisTemplate) {
        this.paymentRepositoryPort = paymentRepositoryPort;
        this.messagePublisherPort = messagePublisherPort;
        this.paymentCompletedEventPublisher = paymentCompletedEventPublisher;
        this.vnPayOutputPort = vnPayOutputPort;
        this.paymentDataMapper = paymentDataMapper;
        this.redisTemplate = redisTemplate;
        this.objectMapper = new ObjectMapper();
    }

    // --- 1. HÀM ĐƯỢC GỌI BỞI CONSUMER  ---
    @Override
    @Transactional
    public void processPaymentFromEvent(OrderEvent event) {
        System.out.println("📥 PaymentService nhận OrderEvent: orderId={}, status={}" + event.getOrderId() + event.getStatus());

        // Cập nhật Cache
        redisTemplate.opsForValue().set(event.getOrderId().toString(), event.getStatus());
        System.out.println("✅ Cập nhật orderStatusCache: " + redisTemplate.opsForValue().get(event.getOrderId().toString()));
        if ("APPROVED".equals(event.getStatus())) {
           System.out.println("Order approved -> Có thể kích hoạt logic thanh toán tự động tại đây nếu cần");
        }

        try {
            // Chuyển OrderEvent thành OrderCreatedEvent (giả sử có mapping)
            // Hoặc lưu trực tiếp nếu event là OrderCreatedEvent
            // Ở đây, giả sử event có đủ fields, nhưng thực tế OrderEvent ít fields hơn.
            // Để fix, ta sẽ lưu JSON của OrderCreatedEvent từ Kafka listener.

            // Nhưng vì interface là OrderEvent, ta sẽ lưu status như cũ, nhưng để processPayment hoạt động, ta cần thay đổi.

            // Thay đổi: Lưu toàn bộ event dưới dạng JSON nếu có thể.

            // Giả sử OrderEvent có thể serialize, nhưng thực tế không.

            // Tốt nhất là thay đổi interface để accept OrderCreatedEvent.

            // Để đơn giản, ta sẽ lưu một object đơn giản với status, amount, etc.

            // Nhưng để fix nhanh, ta sẽ tạo một JSON string với fields cần thiết.

            String json = "{\"orderId\":\"" + event.getOrderId() + "\",\"customerId\":\"" + event.getCustomerId() + "\",\"totalAmount\":" + event.getPrice() + ",\"status\":\"" + event.getStatus() + "\"}";
            redisTemplate.opsForValue().set("PAYMENT_ORDER:" + event.getOrderId(), json, 30, TimeUnit.MINUTES);
            System.out.println("✅ Lưu order vào Redis: " + json);
        } catch (Exception e) {
            System.err.println("Lỗi lưu order vào Redis: " + e.getMessage());
        }
    }

//    @Override
//    @Transactional
//    public PaymentResponse processPayment(CreatePaymentCommand request) {
//        try {
//            Payment payment = paymentDataMapper.paymentRequestToPayment(request);
////        Tạm thời không check để test VNPay
//            payment.initializePayment();
////        payment.validatePayment();
////            payment.setId(new PaymentId(UUID.randomUUID()));
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
@Override
@Transactional
public PaymentResponse processPayment(CreatePaymentCommand paymentRequest) {
    String redisKey = "PAYMENT_ORDER:" + paymentRequest.getOrderId();
    System.out.println("Checking order status from Redis for key: " + redisKey);
    // 1. Lấy dữ liệu từ Redis
    String jsonValue = redisTemplate.opsForValue().get(redisKey);
    log.info("Checking order status from Redis: {}", jsonValue);
    System.out.println("Retrieved JSON from Redis: " + jsonValue);
    if (jsonValue == null) {
        return buildPaymentResponse(paymentRequest, PaymentStatus.FAILED,
                "Đơn hàng không tồn tại hoặc đã hết hạn thanh toán.");
    }

    try {
        // 2. Parse JSON thành Object để kiểm tra
        OrderCreatedEvent redisDto = objectMapper.readValue(jsonValue, OrderCreatedEvent.class);

        if (!"PENDING".equals(redisDto.getStatus())) {
            return buildPaymentResponse(paymentRequest, PaymentStatus.FAILED,
                    "Đơn hàng không ở trạng thái chờ thanh toán. Status: " + redisDto.getStatus());
        }

        // (Optional) Kiểm tra số tiền khớp không
        if (redisDto.getTotalAmount().compareTo(paymentRequest.getAmount()) != 0) {
            return buildPaymentResponse(paymentRequest, PaymentStatus.FAILED, "Số tiền không khớp!");
        }

        // 3. Tạo Payment Entity & Gán ID
        Payment payment = paymentDataMapper.paymentRequestToPayment(paymentRequest);
        payment.initializePayment();

        // QUAN TRỌNG: Lúc này mới tạo link, chưa trả tiền -> Status phải là PENDING
        payment.setPaymentStatus(PaymentStatus.PENDING);
        paymentRepositoryPort.save(payment);

        // 4. Tạo URL thanh toán VNPay
        String vnpTxnRef = payment.getId().getValue().toString(); // Lấy UUID string chuẩn

        // Gọi hàm tạo URL (Lưu ý: Bạn cần sửa hàm generatePaymentUrl để không dùng paymentCache Map nữa)
        // Thay vào đó, nếu cần lưu secureHash để đối soát, ta lưu vào Redis
        String paymentUrl = vnPayOutputPort.generatePaymentUrl(payment, paymentRequest, vnpTxnRef, vnpPayUrl, vnpTmnCode, vnpHashSecret, vnpReturnUrl, paymentCache);

        log.info("Generated VNPay payment URL: {}", paymentUrl);

        // 5. Cập nhật Redis: Đánh dấu là đang xử lý để không tạo link 2 lần
        redisDto.setStatus("PROCESSING");
        redisTemplate.opsForValue().set(redisKey, objectMapper.writeValueAsString(redisDto), 30, TimeUnit.MINUTES);

        // 6. Lưu thông tin đối soát vào Redis (Thay cho paymentCache Map)
        // Key này dùng để verify ở hàm callback. TTL 15 phút.
        redisTemplate.opsForValue().set("VNP_TXN:" + vnpTxnRef, "WAITING", 15, TimeUnit.MINUTES);

        // 7. Trả về Response
        PaymentResponse successResponse = new PaymentResponse();
        successResponse.setPaymentId(payment.getId().getValue());
        successResponse.setOrderId(new OrderId(paymentRequest.getOrderId()));
        successResponse.setAmount(paymentRequest.getAmount());
        successResponse.setStatus(PaymentStatus.PENDING); // Vẫn là PENDING
        successResponse.setMessage("Tạo link thanh toán thành công, vui lòng truy cập URL để trả tiền.");
        successResponse.setPaymentUrl(paymentUrl);
        successResponse.setCreatedAt(payment.getCreatedAt());

        // Lưu ý: Chưa publish PaymentCompletedEvent ở đây vì chưa trả tiền xong
        return successResponse;

    } catch (Exception e) {
        log.error("Lỗi tạo thanh toán: {}", e.getMessage());
        return buildPaymentResponse(paymentRequest, PaymentStatus.FAILED, "Lỗi hệ thống: " + e.getMessage());
    }
}

//    @Override
//    @Transactional
//    public ResponseData handleCallback(Map<String, String> params) {
//        log.info("Nhận callback từ VNPay: {}", params);
//
//        String vnp_SecureHash = params.get("vnp_SecureHash");
//        String billID = params.get("vnp_TxnRef"); // Đây là Payment ID (UUID)
//
//        if (billID == null || vnp_SecureHash == null) {
//            return new ResponseData(400, false, "Dữ liệu callback không hợp lệ (Thiếu params)", null);
//        }
//
//        // 1. Verify Checksum
//        // Hàm verifyChecksum nên tính toán lại hash dựa trên params nhận được và SecretKey
//        // Không cần lấy originalHashData từ cache nếu dùng cơ chế HmacSHA512 chuẩn của VNPay
//        boolean isValid = vnPayOutputPort.verifyChecksum(params, vnpHashSecret);
//
//        if (!isValid) {
//            log.error("Checksum verification failed cho billID: {}", billID);
//            return new ResponseData(400, false, "Chữ ký không hợp lệ (Checksum failed)", null);
//        }
//
//        // 2. Kiểm tra giao dịch có tồn tại trong Redis không (Thay thế paymentCache)
//        String txnStatus = redisTemplate.opsForValue().get("VNP_TXN:" + billID);
//        if (txnStatus == null) {
//            // Có thể giao dịch đã hết hạn hoặc fake request
//            log.warn("Giao dịch {} không tìm thấy trong Redis (hoặc đã timeout)", billID);
//        }
//
//        UUID paymentId = UUID.fromString(billID);
//        Payment payment = paymentRepositoryPort.findById(new PaymentId(paymentId))
//                .orElseThrow(() -> new RuntimeException("Payment not found in DB"));
//
//        // Kiểm tra xem đơn này đã xử lý xong chưa để tránh xử lý 2 lần (Idempotency)
//        if (payment.getPaymentStatus() == PaymentStatus.COMPLETED) {
//            return new ResponseData(200, true, "Đơn hàng đã được thanh toán trước đó", null);
//        }
//
//        String vnpResponseCode = params.get("vnp_ResponseCode");
//        String vnpTransactionStatus = params.get("vnp_TransactionStatus");
//
//        // 3. Xử lý kết quả
//        if ("00".equals(vnpTransactionStatus)) {
//            // --- THÀNH CÔNG ---
//            log.info("Thanh toán thành công cho Payment: {}", paymentId);
//
//            payment.setPaymentStatus(PaymentStatus.COMPLETED);
//            payment.setTransactionId(params.get("vnp_TransactionNo")); // Mã giao dịch VNPay
//            paymentRepositoryPort.save(payment);
//
//            // A. Publish Event quan trọng nhất: PAYMENT COMPLETED
//            // Event này sẽ kích hoạt Order Service chuyển trạng thái sang PAID
//            messagePublisherPort.publish(PaymentCompletedEvent.builder()
//                    .orderId(payment.getOrderId().getValue())
//                    .paymentId(payment.getId().getValue())
//                    .customerId(payment.getCustomerId().getValue())
//                    .amount(payment.getPrice().getAmount())
//                    .transactionId(params.get("vnp_TransactionNo"))
//                    .build());
//
//            // B. Update Redis của Order sang PAID (để flow Order không cho thanh toán lại)
//            String redisKey = "PAYMENT_ORDER:" + payment.getOrderId().getValue();
//            // Lấy object cũ ra, update status, lưu lại (hoặc xóa luôn tùy logic)
//            redisTemplate.delete(redisKey); // Xóa luôn để không ai gọi vào được nữa
//
//            // C. Xóa key transaction tạm
//            redisTemplate.delete("VNP_TXN:" + billID);
//
//            Map<String, Object> data = Map.of("transactionNo", params.get("vnp_TransactionNo"), "orderId", payment.getOrderId().getValue().toString());
//            return new ResponseData(200, true, "Thanh toán thành công", data);
//
//        } else {
//            // --- THẤT BẠI ---
//            log.error("Thanh toán thất bại. Code: {}", vnpResponseCode);
//
//            payment.setPaymentStatus(PaymentStatus.FAILED);
//            payment.setFailureReason("VNPay Error Code: " + vnpResponseCode);
//            paymentRepositoryPort.save(payment);
//
//            // Publish Payment Failed Event
//            messagePublisherPort.publish(new PaymentFailedEvent(payment, "VNPay Error: " + vnpResponseCode));
//
//            // Reset Redis về PENDING để khách có thể thử thanh toán lại (nếu muốn)
//            // Hoặc để nguyên PROCESSING tùy nghiệp vụ
//
//            return new ResponseData(400, false, "Thanh toán thất bại từ phía ngân hàng", null);
//        }
//    }

    // Helper method
    private PaymentResponse buildPaymentResponse(CreatePaymentCommand request, PaymentStatus status, String msg) {
        PaymentResponse r = new PaymentResponse();
        r.setOrderId(new OrderId(request.getOrderId()));
        r.setStatus(status);
        r.setMessage(msg);
        return r;
    }
//public PaymentResponse processPayment(CreatePaymentCommand paymentRequest) {
//    // 1. CHECK REDIS
//    String key = "PAYMENT_ORDER:" + paymentRequest.getOrderId();
//    // 1. Lấy chuỗi JSON từ Redis
//    String jsonValue = redisTemplate.opsForValue().get(key);
//
//    System.out.println("Checking order status from cache: " + paymentRequest.getOrderId() + jsonValue);
//    // Trường hợp 1: Không tìm thấy (Do chưa nhận được Event hoặc Hết hạn 30p)
//    if (jsonValue == null) {
//        PaymentResponse errorResponse = new PaymentResponse();
//        errorResponse.setMessage("Order is not approved for payment. Current status: " + jsonValue);
//        errorResponse.setStatus(PaymentStatus.FAILED);
//        return errorResponse;
//    }
//
//    // Trường hợp 2: Trạng thái không phải PENDING
//    if (!"PENDING".equals(currentStatus)) {
//        PaymentResponse errorResponse = new PaymentResponse();
//        errorResponse.setMessage("Order is not approved for payment. Current status: " + currentStatus);
//        errorResponse.setStatus(PaymentStatus.FAILED);
//        return errorResponse;
//    }
//
//    // 1. Tạo Payment Entity & Gán ID
//    Payment payment = paymentDataMapper.paymentRequestToPayment(paymentRequest);
//    payment.initializePayment();
//    System.out.println("Initialized Payment: " + payment);
//
//    try {
//        // --- TRƯỜNG HỢP THÀNH CÔNG ---
//        payment.setPaymentStatus(PaymentStatus.COMPLETED); // Tạm set là THANH TOÁN THÀNH CÔNG
//        paymentRepositoryPort.save(payment); // Cập nhật DB
//
//        PaymentResponse successResponse = new PaymentResponse();
//        successResponse.setPaymentId(payment.getId().value());
//        successResponse.setOrderId(new OrderId(paymentRequest.getOrderId()));
//        successResponse.setCustomerId(new CustomerId(paymentRequest.getCustomerId()));
//        successResponse.setAmount(paymentRequest.getAmount());
//        successResponse.setStatus(PaymentStatus.COMPLETED);
//        successResponse.setMessage("Tạo thanh toán thành công, hãy tiến hành thanh toán");
//        String vnpTxnRef = payment.getId().toString();
//        String paymentUrl = vnPayOutputPort.generatePaymentUrl(payment, paymentRequest, vnpTxnRef, vnpPayUrl, vnpTmnCode, vnpHashSecret, vnpReturnUrl, paymentCache);
//        log.info("3. Generated VNPay payment URL: {}", paymentUrl);
//        successResponse.setPaymentUrl(paymentUrl); // Dummy URL for simulate
//        successResponse.setCreatedAt(payment.getCreatedAt());
//        successResponse.setUpdatedAt(payment.getUpdatedAt());
//
//        // Sau khi thanh toán thành công, update Redis để không thanh toán lại lần 2
//        redisTemplate.opsForValue().set(key, "PAID"); // Hoặc xóa luôn: redisTemplate.delete(key);
//        // Publish event for SAGA
//        messagePublisherPort.publish(new PaymentCreatedEvent(payment));
//        return successResponse;
//
//    } catch (Exception e) {
//        // --- TRƯỜNG HỢP THẤT BẠI ---
//        log.error("Thanh toán thất bại: {}", e.getMessage());
//
//        payment.setPaymentStatus(PaymentStatus.FAILED);
//        payment.setFailureReason(String.valueOf(List.of(e.getMessage())));
//        paymentRepositoryPort.save(payment); // Cập nhật DB
//
//        // Bắn Event: PAYMENT FAILED
//        log.info("Đang gửi event PaymentFailed...");
//        messagePublisherPort.publish(new PaymentFailedEvent(payment, String.valueOf(List.of(e.getMessage()))));
//
//        PaymentResponse failedResponse = new PaymentResponse();
//        failedResponse.setPaymentId(payment.getId().value());
//        failedResponse.setStatus(PaymentStatus.FAILED);
//        failedResponse.setMessage("Thanh toán thất bại: " + e.getMessage());
//        return failedResponse;
//    }
//}
//
    @Override
    @Transactional
    public ResponseData handleCallback(Map<String, String> params) {
        // Logic from sample: verify checksum, update payment, publish event
        String vnp_SecureHash = params.get("vnp_SecureHash");
        String billID = params.get("vnp_TxnRef");

        if (billID == null || vnp_SecureHash == null) {
            return new ResponseData(400, false, "Missing billID or signature", null);
        }

        // Extract UUID from PaymentId[value=UUID] format
        String uuidString = billID.replace("PaymentId[value=", "").replace("]", "");
        UUID paymentId;
        try {
            paymentId = UUID.fromString(uuidString);
        } catch (IllegalArgumentException e) {
            return new ResponseData(400, false, "Invalid payment ID format", null);
        }

        String originalHashData = paymentCache.get(billID);
        boolean isValid = vnPayOutputPort.verifyChecksum(params, originalHashData, vnpHashSecret);

        if (isValid) {
            paymentCache.remove(billID);
            String transactionStatus = params.get("vnp_TransactionStatus");
            Payment payment = paymentRepositoryPort.findById(new PaymentId(paymentId))
                    .orElseThrow(() -> new RuntimeException("Payment not found"));
            System.out.println("Payment before update: " + payment);
            if ("00".equals(transactionStatus)) {
                payment.complete();
                payment.setTransactionId(params.get("vnp_TransactionNo"));
                // Bắn Integration Event ra ngoài
                PaymentCompletedEvent completedEvent = PaymentCompletedEvent.builder()
                        .orderId(payment.getOrderId().value())
                        .paymentId(payment.getId().getValue())
                        .customerId(payment.getCustomerId().value())
                        .amount(payment.getPrice().getAmount())
                        .transactionId(params.get("vnp_TransactionNo"))
                        .restaurantId(null)
                        .status("PAID")
                        .items(null)
                        .build();
                paymentCompletedEventPublisher.publish(completedEvent);
                System.out.println("ĐÃ BẮN PaymentCompletedEvent cho orderId: {}" + completedEvent.getOrderId());

                // Vẫn giữ Domain Event nội bộ nếu cần (ví dụ: saga, outbox)
//                messagePublisherPort.publish(new com.example.payment.event.PaymentCompletedEvent(payment));
                paymentRepositoryPort.save(payment);
                Map<String, Object> data = Map.of("transactionNo", params.get("vnp_TransactionNo"), "orderId", payment.getOrderId().toString());
                return new ResponseData(200, true, "Thanh toán thành công", data);
            } else {
                payment.fail("VNPay response code: " + params.get("vnp_ResponseCode"));

                // Bắn Failed Event ra ngoài
                PaymentFailedEvent failedEvent = PaymentFailedEvent.builder()
                        .orderId(payment.getOrderId().value())
                        .customerId(payment.getCustomerId().value())
                        .paymentId(payment.getId().getValue())
                        .amount(payment.getPrice().getAmount())
                        .reason("VNPay response code: " + params.get("vnp_ResponseCode"))
                        .status("FAILED")
                        .build();

                messagePublisherPort.publish(failedEvent);
                System.out.println("ĐÃ BẮN PaymentFailedIntegrationEvent cho orderId: {}" + failedEvent.getOrderId());

//                messagePublisherPort.publish(new PaymentFailedEvent(payment, "Failed"));
                paymentRepositoryPort.save(payment);
                return new ResponseData(400, false, "Thanh toán thất bại", null);
            }
        } else {
            return new ResponseData(400, false, "Checksum verification failed", null);
        }
    }

    @Override
    @Transactional
    public void refundPayment(UUID orderId, String reason) {
        Optional<Payment> paymentOpt = paymentRepositoryPort.findByOrderId(new OrderId(orderId));
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            if (payment.getPaymentStatus() == PaymentStatus.COMPLETED) {
                // Comment out VNPay refund API call for manual processing
                // vnPayOutputPort.requestRefund(payment, payment.getTransactionId(), reason, vnpRefundUrl, vnpTmnCode, vnpHashSecret);

                log.info("Manual refund processing for orderId: {}, paymentId: {}, reason: {}", orderId, payment.getId().value(), reason);

                payment.refund();
                paymentRepositoryPort.save(payment);
                messagePublisherPort.publish(new PaymentRefundedEvent(payment));

                log.info("Refund completed manually for orderId: {}", orderId);
            } else {
                log.info("Payment not completed for orderId: {}", orderId);
            }
        } else {
            log.info("Payment not found for orderId: {}", orderId);
        }
    }

    @Override
    public void setOrderStatusForSimulation(UUID orderId, String status) {
        redisTemplate.opsForValue().set(orderId.toString(), status);
    }

    @Override
    public List<PaymentResponse> getAllPayments() {
        List<Payment> payments = paymentRepositoryPort.findAll();
        return payments.stream()
                .map(payment -> paymentDataMapper.paymentToPaymentResponse(payment, "Retrieved", null))
                .toList();
    }
}
