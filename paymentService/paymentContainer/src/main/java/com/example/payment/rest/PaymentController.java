//package com.example.payment.rest;
//
//import com.example.payment.dto.*;
//import com.example.payment.ports.input.service.PaymentApplicationService;
//import jakarta.validation.Valid;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.UUID;
//
///**
// * REST Controller - PRIMARY ADAPTER
// * Chịu trách nhiệm nhận HTTP requests và gọi vào Application Layer.
// */
//@Slf4j
//@RestController
//@RequestMapping(value = "/api/v1/payments")
//public class PaymentController {
//
//    private final PaymentApplicationService paymentApplicationService;
//
//    public PaymentController(PaymentApplicationService paymentApplicationService) {
//        this.paymentApplicationService = paymentApplicationService;
//    }
//
//    /**
//     * Endpoint để tạo Payment
//     */
//    @PostMapping
//    public ResponseEntity<CreatePaymentResponse> createPayment(
//            @Valid @RequestBody CreatePaymentCommand createPaymentCommand) {
//
//        log.info("Received request to create payment for order: {} and customer: {}",
//                createPaymentCommand.orderId(), createPaymentCommand.customerId());
//
//        try {
//            CreatePaymentResponse response =
//                    paymentApplicationService.createPayment(createPaymentCommand);
//
//            log.info("Payment created successfully with ID: {}", response.paymentId());
//
//            return ResponseEntity.status(HttpStatus.CREATED).body(response);
//        } catch (Exception e) {
//            log.error("Error creating payment: {}", e.getMessage(), e);
//            throw e;
//        }
//    }
//
//    /**
//     * Endpoint để hủy Payment - SAGA Compensation Transaction
//     */
//    @PostMapping("/{paymentId}/cancel")
//    public ResponseEntity<CancelPaymentResponse> cancelPayment(
//            @PathVariable String paymentId,
//            @Valid @RequestBody CancelPaymentCommand cancelPaymentCommand) {
//
//        log.info("Received request to cancel payment: {} with reason: {}",
//                paymentId, cancelPaymentCommand.failureReason());
//
//        try {
//            UUID uuid = UUID.fromString(paymentId);
//
//            CancelPaymentCommand command = new CancelPaymentCommand(
//                    uuid,
//                    cancelPaymentCommand.failureReason()
//            );
//
//            CancelPaymentResponse response =
//                    paymentApplicationService.cancelPayment(command);
//
//            log.info("Payment cancelled successfully: {}", paymentId);
//
//            return ResponseEntity.ok(response);
//        } catch (IllegalArgumentException e) {
//            log.error("Invalid UUID format: {}", paymentId);
//            return ResponseEntity.badRequest().build();
//        } catch (Exception e) {
//            log.error("Error cancelling payment: {}", e.getMessage(), e);
//            throw e;
//        }
//    }
//
//    /**
//     * Endpoint để theo dõi trạng thái Payment
//     */
//    @GetMapping("/{paymentId}")
//    public ResponseEntity<?> getPayment(@PathVariable String paymentId) {
//
//        log.info("Received request to track payment: {}", paymentId);
//
//        try {
//            UUID uuid = UUID.fromString(paymentId);
//            TrackPaymentQuery trackPaymentQuery = new TrackPaymentQuery(uuid);
//
//            TrackPaymentResponse response = paymentApplicationService.trackPayment(trackPaymentQuery);
//
//            log.info("Returning payment status: {} for payment: {}",
//                    response.status(), response.paymentId());
//
//            return ResponseEntity.ok(response);
//
//        } catch (IllegalArgumentException e) {
//            log.error("Invalid UUID format: {}", paymentId);
//            return ResponseEntity
//                    .badRequest()
//                    .body("Invalid payment ID format. Must be UUID.");
//
//        } catch (RuntimeException e) {
//            log.error("Payment not found: {}", paymentId, e);
//            return ResponseEntity
//                    .status(HttpStatus.NOT_FOUND)
//                    .body("Payment not found with ID: " + paymentId);
//
//        } catch (Exception e) {
//            log.error("Error tracking payment: {}", e.getMessage(), e);
//            return ResponseEntity
//                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Error retrieving payment: " + e.getMessage());
//        }
//    }
//
//    /**
//     * Health check endpoint
//     */
//    @GetMapping("/health")
//    public ResponseEntity<String> healthCheck() {
//        log.info("Payment service health check");
//        return ResponseEntity.ok("Payment Service is healthy");
//    }
//}




package com.example.payment.rest;

import com.example.payment.dto.*;
import com.example.payment.ports.input.service.PaymentApplicationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller - PRIMARY ADAPTER
 * Chịu trách nhiệm nhận HTTP requests và gọi vào Application Layer.
 * Exception handling được ủy quyền cho GlobalExceptionHandler.
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/v1/payments")
public class PaymentController {

    private final PaymentApplicationService paymentApplicationService;

    public PaymentController(PaymentApplicationService paymentApplicationService) {
        this.paymentApplicationService = paymentApplicationService;
    }

    /**
     * Endpoint để tạo Payment
     */
    @PostMapping
    public ResponseEntity<CreatePaymentResponse> createPayment(
            @Valid @RequestBody CreatePaymentCommand createPaymentCommand) {

        log.info("Received request to create payment for order: {} and customer: {}",
                createPaymentCommand.orderId(), createPaymentCommand.customerId());

        CreatePaymentResponse response =
                paymentApplicationService.createPayment(createPaymentCommand);

        log.info("Payment created successfully with ID: {}", response.paymentId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Endpoint để hủy Payment - SAGA Compensation Transaction
     */
    @PostMapping("/{paymentId}/cancel")
    public ResponseEntity<CancelPaymentResponse> cancelPayment(
            @PathVariable String paymentId,
            @Valid @RequestBody CancelPaymentCommand cancelPaymentCommand) {

        log.info("Received request to cancel payment: {} with reason: {}",
                paymentId, cancelPaymentCommand.failureReason());

        UUID uuid = UUID.fromString(paymentId); // Throw IllegalArgumentException if invalid

        CancelPaymentCommand command = new CancelPaymentCommand(
                uuid,
                cancelPaymentCommand.failureReason()
        );

        CancelPaymentResponse response =
                paymentApplicationService.cancelPayment(command);

        log.info("Payment cancelled successfully: {}", paymentId);

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint để theo dõi trạng thái Payment
     */
    @GetMapping("/{paymentId}")
    public ResponseEntity<TrackPaymentResponse> getPayment(@PathVariable String paymentId) {

        log.info("Received request to track payment: {}", paymentId);

        UUID uuid = UUID.fromString(paymentId); // Throw IllegalArgumentException if invalid
        TrackPaymentQuery trackPaymentQuery = new TrackPaymentQuery(uuid);

        TrackPaymentResponse response = paymentApplicationService.trackPayment(trackPaymentQuery);

        log.info("Returning payment status: {} for payment: {}",
                response.status(), response.paymentId());

        return ResponseEntity.ok(response);
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