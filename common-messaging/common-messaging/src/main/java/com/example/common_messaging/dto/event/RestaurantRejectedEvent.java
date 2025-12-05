package com.example.common_messaging.dto.event;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class RestaurantRejectedEvent {
    private UUID orderId;
    private UUID restaurantId;
    private UUID sagaId;
    private String rejectionReason;
}
