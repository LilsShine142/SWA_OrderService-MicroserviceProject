package com.example.order.application.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class OrderPaidEvent {
    private UUID orderId;
    private UUID restaurantId;
    private List<OrderItemDto> items;

    @Data
    @Builder
    public static class OrderItemDto {
        private UUID productId;
        private String productName;
        private Integer quantity;
        private BigDecimal price;
    }
}

