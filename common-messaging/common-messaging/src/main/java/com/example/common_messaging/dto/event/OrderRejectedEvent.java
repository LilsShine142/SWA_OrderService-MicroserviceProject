package com.example.common_messaging.dto.event;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

/**
 * Integration Event (DTO) – Gửi từ Restaurant → Order
 * khi từ chối đơn → kích hoạt compensation.
 */
@Data
@Builder
public class OrderRejectedEvent {
    private UUID orderId;
    private UUID sagaId;
    private UUID restaurantId;
    private String reason;
}