package com.example.payment.rest;

import com.example.payment.dto.*;
import com.example.payment.ports.input.service.PaymentApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * REST Controller - PRIMARY ADAPTER
 * Chịu trách nhiệm nhận HTTP requests và gọi vào Application Layer.
 * Tuân thủ Clean Architecture - Presentation Layer
 */

@RestController
@RequestMapping(value = "/api/v1/payments")
public class PaymentController {

    // Inject "Cổng Vào" (Input Port) của Application Layer
    private final PaymentApplicationService paymentApplicationService;

    // Constructor Injection
    public PaymentController(PaymentApplicationService paymentApplicationService) {
        this.paymentApplicationService = paymentApplicationService;
    }

    /**
     * Endpoint để tạo Payment
     */
    @PostMapping
    public ResponseEntity<CreatePaymentResponse> createPayment(
            @Valid @RequestBody CreatePaymentCommand createPaymentCommand) {

        // Gọi vào Application Layer (Input Port)
        CreatePaymentResponse response =
                paymentApplicationService.createPayment(createPaymentCommand);

        // Trả về HTTP 201 Created
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Endpoint để hủy Payment - SAGA Compensation Transaction
     */
    @PostMapping("/{paymentId}/cancel")
    public ResponseEntity<CancelPaymentResponse> cancelPayment(
            @PathVariable UUID paymentId,
            @Valid @RequestBody CancelPaymentCommand cancelPaymentCommand) {

        // Tạo command mới với paymentId từ path variable
        CancelPaymentCommand command = new CancelPaymentCommand(
                paymentId,
                cancelPaymentCommand.failureReason()
        );

        // Gọi vào Application Layer - SAGA Compensation
        CancelPaymentResponse response =
                paymentApplicationService.cancelPayment(command);

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint để theo dõi trạng thái Payment
     */
    @GetMapping("/{paymentId}")
    public ResponseEntity<TrackPaymentResponse> getPayment(
            @PathVariable UUID paymentId) {

        // Tạo Query DTO
        TrackPaymentQuery trackPaymentQuery = new TrackPaymentQuery(paymentId);

        // Gọi vào Application Layer
        TrackPaymentResponse response = paymentApplicationService.trackPayment(trackPaymentQuery);

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint để lấy tất cả payments theo order
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByOrder(
            @PathVariable UUID orderId) {

        // TODO: Implement in service layer
        // List<PaymentResponse> responses = paymentApplicationService.getPaymentsByOrder(orderId);

        return ResponseEntity.ok(Collections.emptyList());
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Payment Service is healthy");
    }
}