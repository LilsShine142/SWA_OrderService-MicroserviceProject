package com.example.restaurant.container.rest;

import com.example.restaurant.application.dto.request.ApproveOrderCommand;
import com.example.restaurant.application.dto.response.OrderApprovalResponse;
import com.example.restaurant.application.ports.input.service.RestaurantApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller cho Restaurant Service.
 */
@Slf4j
@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantApplicationService restaurantApplicationService;

    /**
     * Duyệt đơn hàng – SAGA Step 2
     * Nhận từ Order Service qua Kafka (OrderCreatedEvent)
     * Nhưng cũng cung cấp API để test thủ công
     */
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
    @PostMapping("/{restaurantId}/orders/{orderId}/reject")
    public ResponseEntity<Void> rejectOrder(
            @PathVariable UUID restaurantId,
            @PathVariable UUID orderId,
            @RequestParam String reason) {

        log.info("Nhận yêu cầu từ chối đơn: orderId={}, reason={}", orderId, reason);

        restaurantApplicationService.rejectOrder(orderId, reason);

        return ResponseEntity.ok().build();
    }

    /**
     * API test call api
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Restaurant Service is UP");
    }
}
