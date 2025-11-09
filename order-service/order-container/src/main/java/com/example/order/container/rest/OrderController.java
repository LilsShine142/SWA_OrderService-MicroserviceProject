package com.example.order.container.rest;




import com.example.order.application.dto.CreateOrderCommand;
import com.example.order.application.dto.CreateOrderResponse;
import com.example.order.application.dto.TrackOrderQuery;
import com.example.order.application.dto.TrackOrderResponse;
import com.example.order.application.ports.input.service.OrderApplicationService; // Input Port
import jakarta.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller - PRIMARY ADAPTER [cite: 237]
 * Chịu trách nhiệm nhận HTTP requests và gọi vào Application Layer.
 */
@Slf4j // Tự động tạo logger
@RestController
@RequestMapping(value = "/orders") // Endpoint gốc
public class OrderController {

    // Inject "Cổng Vào" (Input Port) của Application Layer [cite: 217-218]
    private final OrderApplicationService orderApplicationService;

    // Constructor Injection [cite: 220]
    public OrderController(OrderApplicationService orderApplicationService) {
        this.orderApplicationService = orderApplicationService;
    }

    /**
     * Endpoint để tạo Order [cite: 245-262].
     */
    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(
            @Valid @RequestBody CreateOrderCommand createOrderCommand) { // [cite: 247]
       // log.info("Creating order for customer: {} at restaurant: {}",
         //       createOrderCommand.customerId(),
            //    createOrderCommand.restaurantId()); // [cite: 249-251]

        // Gọi vào Application Layer (Input Port) [cite: 252-254]
        CreateOrderResponse response =
                orderApplicationService.createOrder(createOrderCommand);

       // log.info("Order created with tracking id: {}",
           //     response.orderTrackingId()); // [cite: 256-257]

        // Trả về HTTP 201 Created [cite: 258-262]
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Endpoint để theo dõi Order.
     */
    @GetMapping("/{trackingId}")
    public ResponseEntity<TrackOrderResponse> trackOrder(
            @PathVariable UUID trackingId) {

        // Tạo Query DTO
        TrackOrderQuery trackOrderQuery = new TrackOrderQuery(trackingId);
       // log.info("Tracking order with tracking id: {}", trackingId);

        // Gọi vào Application Layer
        TrackOrderResponse response = orderApplicationService.trackOrder(trackOrderQuery);

       // log.info("Returning order status: {} for tracking id: {}",
               // response.orderStatus(), response.orderTrackingId());

        // Trả về HTTP 200 OK
        return ResponseEntity.ok(response);
    }

    // (Bạn có thể thêm các endpoint khác như PUT /cancel...)
}