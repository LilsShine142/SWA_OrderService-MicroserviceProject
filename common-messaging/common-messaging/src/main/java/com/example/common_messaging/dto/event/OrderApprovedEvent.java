package com.example.common_messaging.dto.event;

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
public class OrderApprovedEvent {
    private UUID orderId;
    private UUID restaurantId;
    private String status;
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
