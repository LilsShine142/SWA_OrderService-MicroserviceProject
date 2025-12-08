package com.example.order.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor // <--- QUAN TRỌNG: Để Jackson khởi tạo object
@AllArgsConstructor
public class OrderPaidEvent {
    private UUID orderId;
    private UUID customerId;
    private UUID restaurantId;
    private String status; // Thêm trường status
    private List<OrderItemDto> items;

    @Data
    @Builder
    @NoArgsConstructor // <--- QUAN TRỌNG: Để Jackson khởi tạo object
    @AllArgsConstructor
    public static class OrderItemDto {
        private UUID productId;
        private String productName;
        private Integer quantity;
        private BigDecimal price;
    }
}

