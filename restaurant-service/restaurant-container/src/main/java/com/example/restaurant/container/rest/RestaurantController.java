package com.example.restaurant.container.rest;

import com.example.restaurant.application.dto.request.ApproveOrderCommand;
import com.example.restaurant.application.dto.request.RejectOrderCommand;
import com.example.restaurant.application.dto.response.ApiResponse;
import com.example.restaurant.application.dto.response.OrderApprovalResponse;
import com.example.restaurant.application.ports.input.service.RestaurantApplicationService;
import com.example.restaurant.domain.core.entity.OrderApproval;
import com.example.restaurant.domain.core.entity.Restaurant;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/getall")
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {
        log.info("Getting all restaurants");
        List<Restaurant> restaurants = restaurantApplicationService.getAllRestaurants();
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("/menu-items/getall")
    public ResponseEntity<List<?>> getAllMenuItems() {
        log.info("Getting all menu items");
        // TODO: Implement when MenuItem entity is available
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/order-approvals/getall")
    public ResponseEntity<List<OrderApproval>> getAllOrderApprovals() {
        log.info("Getting all order approvals");
        List<OrderApproval> approvals = restaurantApplicationService.getAllOrderApprovals();
        return ResponseEntity.ok(approvals);
    }
}