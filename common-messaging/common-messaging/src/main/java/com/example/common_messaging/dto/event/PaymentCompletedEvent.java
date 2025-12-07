package com.example.common_messaging.dto.event;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class PaymentCompletedEvent {
    private UUID orderId;
    private UUID customerId;
    private UUID restaurantId;
    private List<OrderItemDto> items;

    @Data
    @Builder
    public static class OrderItemDto {
        private UUID productId;
        private Integer quantity;
        private java.math.BigDecimal price;
    }
}
