package com.example.restaurant.container.rest;

import com.example.restaurant.application.dto.request.ApproveOrderCommand;
import com.example.restaurant.application.dto.request.RejectOrderCommand;
import com.example.restaurant.application.dto.response.OrderApprovalResponse;
import com.example.restaurant.application.ports.input.service.RestaurantApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller cho Restaurant Service.
 */
@Slf4j
@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantApplicationService restaurantApplicationService;

    /**
     * Duyệt đơn hàng – SAGA Step 2
     * Nhận từ Order Service qua Kafka (OrderCreatedEvent)
     * Nhưng cũng cung cấp API để test thủ công
     */
    @Operation(summary = "Duyệt đơn hàng")
    @ApiResponse(responseCode = "200", description = "Order approved successfully")
    @PostMapping("/{restaurantId}/orders/{orderId}/approve")
    public ResponseEntity<OrderApprovalResponse> approveOrder(
            @PathVariable UUID restaurantId,
            @PathVariable UUID orderId,
            @RequestBody @Valid ApproveOrderCommand command) {

        OrderApprovalResponse response = restaurantApplicationService.approveOrder(command);
        return ResponseEntity.ok(response);
    }

    /**
     * Từ chối đơn hàng – SAGA Compensation
     */
    @Operation(summary = "Từ chối đơn hàng")
    @ApiResponse(responseCode = "200", description = "Order rejected successfully")
    @PostMapping("/{restaurantId}/orders/{orderId}/reject")
    public ResponseEntity<Void> rejectOrder(
            @PathVariable UUID restaurantId,
            @PathVariable UUID orderId,
            @RequestBody @Valid RejectOrderCommand command) {

        log.info("Nhận yêu cầu từ chối đơn: orderId={}, reason={}", orderId, command.getReason());

        restaurantApplicationService.rejectOrder(orderId, command.getReason());

        return ResponseEntity.ok().build();
    }

    /**
     * API test call api
     */
    @Operation(summary = "Health check")
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Restaurant Service is UP");
    }
}