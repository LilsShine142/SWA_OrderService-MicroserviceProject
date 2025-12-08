package com.example.payment.dto;

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
public class PaymentCompletedEvent {
    private UUID orderId;
    private UUID paymentId;
    private UUID customerId;
    private String transactionId;
    private BigDecimal amount;
    private UUID restaurantId;
    private String status;
    private List<com.example.common_messaging.dto.event.PaymentCompletedEvent.OrderItemDto> items;

    @Data
    @Builder
    @NoArgsConstructor // <--- QUAN TRỌNG: Để Jackson khởi tạo object
    @AllArgsConstructor
    public static class OrderItemDto {
        private UUID productId;
        private Integer quantity;
        private java.math.BigDecimal price;
    }
}
