package com.example.common_messaging.dto.event;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

/**
 * Integration Event (DTO) – Gửi từ Restaurant → Order
 * khi duyệt đơn thành công.
 */
@Data
@Builder
public class OrderApprovedEvent {
    private UUID orderId;
    private UUID sagaId;
    private UUID restaurantId;
}