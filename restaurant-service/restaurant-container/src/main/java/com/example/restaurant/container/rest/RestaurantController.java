package com.example.restaurant.container.rest;

import com.example.restaurant.application.dto.request.ApproveOrderCommand;
import com.example.restaurant.application.dto.request.RejectOrderCommand;
import com.example.restaurant.application.dto.response.ApiResponse;
import com.example.restaurant.application.dto.response.OrderApprovalResponse;
import com.example.restaurant.application.ports.input.service.RestaurantApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/approve")
    public ResponseEntity<ApiResponse<OrderApprovalResponse>> approveOrder(
            @RequestBody @Valid ApproveOrderCommand command) {

        OrderApprovalResponse response = restaurantApplicationService.approveOrder(command);

        if (response.isSuccess()) {
            return ResponseEntity.ok(ApiResponse.success("Thành công", response));
        } else {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail(response.getMessage()));
        }
    }

    /**
     * Từ chối đơn hàng – SAGA Compensation
     */
    @PostMapping("/reject")
    public ResponseEntity<ApiResponse<OrderApprovalResponse>> rejectOrder(
            @RequestBody @Valid RejectOrderCommand command) {

        OrderApprovalResponse response = restaurantApplicationService.rejectOrder(command);

        if (response.isSuccess()) {
            return ResponseEntity.ok(ApiResponse.success("Thành công", response));
        } else {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.fail(response.getMessage()));
        }
    }

    /**
     * API test call api
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Restaurant Service is UP");
    }
}