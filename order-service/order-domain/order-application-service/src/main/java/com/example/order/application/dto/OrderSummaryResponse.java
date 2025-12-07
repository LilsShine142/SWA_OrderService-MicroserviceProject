package com.example.order.application.dto;

import com.example.order.domain.core.entity.OrderItem;
import com.example.order.domain.core.valueobject.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryResponse {
    private UUID orderId;
    private UUID orderTrackingId;
    private UUID customerId;
    private List<OrderItem> items;
    private BigDecimal totalAmount;
    private OrderStatus orderStatus;
    private UUID restaurantId;
    private String fullAddress; // Địa chỉ gộp lại cho gọn: "Street, City"
    private Instant createdAt;
}