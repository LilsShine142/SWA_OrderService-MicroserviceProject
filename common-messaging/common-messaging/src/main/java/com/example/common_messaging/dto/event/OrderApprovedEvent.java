package com.example.common_messaging.dto.event;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
@Data
public class OrderApprovedEvent {
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
