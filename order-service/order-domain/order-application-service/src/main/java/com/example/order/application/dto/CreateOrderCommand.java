package com.example.order.application.dto;


// Bạn cần thêm dependency 'spring-boot-starter-validation'
// vào pom.xml của 'order-application-service' để dùng các @NotNull
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * DTO (Command) cho việc tạo đơn hàng[cite: 247].
 * Sử dụng 'record' cho DTO bất biến.
 */
public record CreateOrderCommand(
        @NotNull UUID customerId, // [cite: 194]
        @NotNull UUID restaurantId, // [cite: 195]
        @NotNull BigDecimal price, // [cite: 196]

        @NotNull @Valid List<OrderItemCommand> items, // [cite: 197]
        @NotNull @Valid OrderAddress address // [cite: 205]
) {

    /**
     * DTO con cho OrderItem
     */
    public record OrderItemCommand(
            @NotNull UUID productId, // [cite: 199]
            @NotNull Integer quantity, // [cite: 200]
            @NotNull BigDecimal price, // [cite: 201]
            @NotNull BigDecimal subTotal // [cite: 202]
    ) {}

    /**
     * DTO con cho Address
     */
    public record OrderAddress(
            @NotNull String street, // [cite: 206]
            @NotNull String postalCode, // [cite: 207]
            @NotNull String city // [cite: 208]
    ) {}
}