package com.example.order.application.dto;

import com.example.order.domain.core.valueobject.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryResponse {
    private UUID orderTrackingId;
    private UUID customerId;
    private BigDecimal totalAmount;
    private OrderStatus orderStatus;
    private String fullAddress; // Địa chỉ gộp lại cho gọn: "Street, City"
    private Instant createdAt;
}