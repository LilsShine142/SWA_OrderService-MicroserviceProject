package com.example.payment.rest;

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
    public ResponseEntity<String> refundPayment(@RequestParam UUID paymentId, @RequestParam String transactionNo, @RequestParam String reason) {
        paymentApplicationService.refundPayment(paymentId, transactionNo, reason);
        return ResponseEntity.ok("Refund processed");
    }

    @GetMapping("/callback")
    public ResponseEntity<String> handleCallback(@RequestParam Map<String, String> params) {
        paymentApplicationService.handleCallback(params);
        return ResponseEntity.ok("Callback processed");
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        log.info("Payment service health check");
        return ResponseEntity.ok("Payment Service is healthy");
    }

}