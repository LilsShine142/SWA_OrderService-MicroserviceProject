package com.example.order.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * DTO (Command) cho việc cập nhật đơn hàng.
 */
public record UpdateOrderCommand(
        @NotNull UUID orderId,
        @NotNull UUID customerId,
        @NotNull UUID restaurantId,
        @NotNull BigDecimal price,
        @NotNull @Valid List<OrderItemCommand> items,
        @NotNull String street,
        @NotNull String postalCode,
        @NotNull String city
) {

    /**
     * DTO con cho OrderItem
     */
    public record OrderItemCommand(
            @NotNull UUID productId,
            @NotNull Integer quantity,
            @NotNull BigDecimal price,
            @NotNull BigDecimal subTotal
    ) {}
}
