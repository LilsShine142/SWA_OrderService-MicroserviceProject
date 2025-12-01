package com.example.payment.rest;

import com.example.payment.dto.*;
import com.example.payment.ports.input.service.PaymentApplicationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST Controller - PRIMARY ADAPTER
 * Chịu trách nhiệm nhận HTTP requests và gọi vào Application Layer.
 * Tích hợp VNPay sandbox theo chuẩn, với callback/IPN/refund.
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/payments")
public class PaymentController {

    private final PaymentApplicationService paymentApplicationService;

    public PaymentController(PaymentApplicationService paymentApplicationService) {
        this.paymentApplicationService = paymentApplicationService;
    }

    /**
     * Endpoint để tạo Payment và generate VNPay URL (sandbox)
     */
    @PostMapping
    public ResponseEntity<CreatePaymentResponse> createPayment(
            @Valid @RequestBody CreatePaymentCommand createPaymentCommand) {

        log.info("Received request to create payment for order: {} and customer: {}",
                createPaymentCommand.orderId(), createPaymentCommand.customerId());

        CreatePaymentResponse response =
                paymentApplicationService.createPayment(createPaymentCommand);

        log.info("Payment created successfully with ID: {} and VNPay URL: {}", response.paymentId(), response.paymentUrl());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Endpoint để hủy Payment - SAGA Compensation Transaction
     */
    @PostMapping("/{paymentId}/cancel")
    public ResponseEntity<CancelPaymentResponse> cancelPayment(
            @PathVariable String paymentId,
            @RequestBody Map<String, String> requestBody) {

        String reason = requestBody.get("reason");
        if (reason == null) {
            reason = "No reason provided";
        }

        log.info("Received request to cancel payment: {} with reason: {}", paymentId, reason);

        UUID uuid = UUID.fromString(paymentId);

        CancelPaymentCommand command = new CancelPaymentCommand(uuid, reason);

        CancelPaymentResponse response = paymentApplicationService.cancelPayment(command);

        log.info("Payment cancelled successfully: {}", paymentId);

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint để theo dõi trạng thái Payment
     */
    @GetMapping("/{paymentId}")
    public ResponseEntity<TrackPaymentResponse> getPayment(@PathVariable String paymentId) {

        log.info("Received request to track payment: {}", paymentId);

        UUID uuid = UUID.fromString(paymentId);
        TrackPaymentQuery trackPaymentQuery = new TrackPaymentQuery(uuid);

        TrackPaymentResponse response = paymentApplicationService.trackPayment(trackPaymentQuery);

        log.info("Returning payment status: {} for payment: {}",
                response.status(), response.paymentId());

        return ResponseEntity.ok(response);
    }

    /**
     * VNPay Callback Endpoint (sandbox return)
     */
    @GetMapping("/callback")
    public ResponseEntity<ResponseData> handlePaymentReturn(@RequestParam Map<String, String> params) {

        log.info("VNPay callback received: {}", params);

        ResponseData response = paymentApplicationService.handleVnpayCallback(params);

        return ResponseEntity.ok(response);
    }

    /**
     * VNPay IPN Endpoint (for async notification)
     */
    @GetMapping("/ipn")
    public ResponseEntity<ResponseData> handlePaymentIPN(@RequestParam Map<String, String> params) {

        log.info("VNPay IPN received: {}", params);

        ResponseData response = paymentApplicationService.handleVnpayCallback(params);  // Same logic as callback

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint để refund Payment (sandbox)
     */
    @PostMapping("/refund")
    public ResponseEntity<ResponseData> refund(@RequestBody @Valid RefundPaymentCommand command) {

        log.info("Received request to refund payment for paymentId: {}", command.getPaymentId());

        try {
            ResponseData response = paymentApplicationService.refundPayment(command);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error during refund: {}", e.getMessage());
            ResponseData errorResponse = new ResponseData();
            errorResponse.setStatus(500);
            errorResponse.setMessage("Refund failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        log.info("Payment service health check");
        return ResponseEntity.ok("Payment Service is healthy");
    }

    /**
     * Endpoint để lấy danh sách tất cả Payments
     */
    @GetMapping
    public ResponseEntity<List<TrackPaymentResponse>> getAllPayments() {
        log.info("Getting all payments");

        List<TrackPaymentResponse> responses = paymentApplicationService.getAllPayments();

        log.info("Returning {} payments", responses.size());

        return ResponseEntity.ok(responses);
    }
}