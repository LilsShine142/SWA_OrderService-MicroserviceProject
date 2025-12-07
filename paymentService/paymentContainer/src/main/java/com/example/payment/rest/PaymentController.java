package com.example.payment.rest;

import com.example.common_messaging.dto.event.OrderCreatedEvent;
import com.example.payment.dto.*;
import com.example.payment.ports.input.service.PaymentApplicationService;
import com.example.payment.valueobject.PaymentStatus;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentApplicationService paymentApplicationService;

    public PaymentController(PaymentApplicationService paymentApplicationService) {
        this.paymentApplicationService = paymentApplicationService;
    }

//    @PostMapping
//    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody CreatePaymentCommand request) {
//        log.info("1. Received payment creation request for orderId: {}", request.getOrderId());
//        PaymentResponse response = paymentApplicationService.processPayment(request);
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }
@PostMapping
public ResponseEntity<PaymentResponse> processPayment(@RequestBody CreatePaymentCommand paymentRequest) {
    log.info("Nhận yêu cầu thanh toán cho OrderId: {}", paymentRequest.getOrderId());

    PaymentResponse response = paymentApplicationService.processPayment(paymentRequest);

    if (response.getStatus() == PaymentStatus.COMPLETED) {
        // Thành công: Trả về 200 OK
        return ResponseEntity.ok(response);
    } else {
        // Thất bại (Logic nghiệp vụ): Vẫn trả về 200 hoặc 400 tùy bạn,
        // nhưng thường trong SAGA vẫn trả về object chứa lý do lỗi.
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}

    @PostMapping("/refund")
    public ResponseEntity<String> refundPayment(@RequestParam UUID orderId, @RequestParam String reason) {
        paymentApplicationService.refundPayment(orderId, reason);
        return ResponseEntity.ok("Refund initiated");
    }

    @GetMapping("/getall")
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        List<PaymentResponse> payments = paymentApplicationService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

//    @GetMapping("/callback")
//    public ResponseEntity<?> handleCallback(@RequestParam Map<String, String> params) {
//        try {
//            log.info("VNPay Callback Params: {}", params);
//
//            // Gọi service xử lý logic
//            PaymentCallbackResponse callbackResponse = paymentApplicationService.handleCallback(params);
//
//            if (callbackResponse.isSuccess()) {
//                // Cách 1: Trả về thông báo text đơn giản
//                return ResponseEntity.ok("Thanh toán THÀNH CÔNG! Mã GD: " + params.get("vnp_TxnRef"));
//
//                // Cách 2 (Nâng cao): Redirect về Frontend (React/Angular/Thymeleaf)
//                // return ResponseEntity.status(HttpStatus.FOUND)
//                //        .header("Location", "http://localhost:3000/payment-success")
//                //        .build();
//            } else {
//                return ResponseEntity.badRequest().body("Thanh toán THẤT BẠI. Lý do: " + callbackResponse.getMessage());
//            }
//        } catch (Exception e) {
//            log.error("Lỗi xử lý callback VNPay: ", e);
//            return ResponseEntity.internalServerError().body("Lỗi hệ thống khi xử lý thanh toán: " + e.getMessage());
//        }
//    }
@GetMapping("/callback")
public ResponseEntity<ResponseData> handlePaymentReturn(@RequestParam Map<String, String> params) throws Exception {
    ResponseData response = paymentApplicationService.handleCallback(params);
    return ResponseEntity.status(response.getStatus()).body(response);
}

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        log.info("Payment service health check");
        return ResponseEntity.ok("Payment Service is healthy");
    }

    // API này giả vờ như Payment Service vừa nhận được tin nhắn từ Kafka về order approved
    @PostMapping("/simulate/order-created")
    public ResponseEntity<String> simulateOrderCreated(@RequestBody SimulateOrderCreatedRequest request) {
        log.info("Simulate OrderCreated for orderId={} customerId={} amount={}",
                request.getOrderId(), request.getCustomerId(), request.getTotalAmount());

        // Giả lập order đã được approved
        paymentApplicationService.setOrderStatusForSimulation(request.getOrderId(), "APPROVED");

        log.info("Order status set to APPROVED for orderId={}", request.getOrderId());
        return ResponseEntity.ok("Order simulated as approved. Now you can call /api/payments to create payment.");
    }

}