package com.example.common_messaging.dto.event;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class OrderPaidEvent {
    private UUID orderId;
    private UUID customerId;
    private UUID restaurantId;
    private String status; // Thêm trường status
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
