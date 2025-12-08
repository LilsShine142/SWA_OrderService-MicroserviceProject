package com.example.order.container.rest;

import com.example.order.application.dto.*;
import com.example.order.application.ports.input.service.OrderApplicationService;
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
@RequestMapping(value = "/api/orders")
public class OrderController {

    private final OrderApplicationService orderApplicationService;

    public OrderController(OrderApplicationService orderApplicationService) {
        this.orderApplicationService = orderApplicationService;
    }

    // 1. User gọi API Tạo đơn
    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(@Valid @RequestBody CreateOrderCommand createOrderCommand) {
        log.info("REST request to create order for customer: {}", createOrderCommand.getCustomerId());
        CreateOrderResponse response = orderApplicationService.createOrder(createOrderCommand);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 2. User gọi API xem trạng thái đơn
    @GetMapping("/{trackingId}")
    public ResponseEntity<TrackOrderResponse> trackOrder(@PathVariable UUID trackingId) {
        TrackOrderResponse response = orderApplicationService.trackOrder(new TrackOrderQuery(trackingId));
        return ResponseEntity.ok(response);
    }

    // 3. User gọi API Hủy đơn
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<CancelOrderResponse> cancelOrder(
            @PathVariable UUID trackingId,
            @RequestBody Map<String, String> requestBody) {

        String reason = requestBody.getOrDefault("reason", "No reason provided");
        CancelOrderCommand command = new CancelOrderCommand(trackingId, reason);

        CancelOrderResponse response = orderApplicationService.cancelOrder(command);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getall")
    public ResponseEntity<List<OrderSummaryResponse>> getAllOrders() {
        // log.info("REST request to get all orders");
        List<OrderSummaryResponse> response = orderApplicationService.findAllOrders();
        return ResponseEntity.ok(response);
    }
}