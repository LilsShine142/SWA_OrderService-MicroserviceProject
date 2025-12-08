package com.example.common_messaging.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor // <--- QUAN TRỌNG: Để Jackson khởi tạo object
@AllArgsConstructor
public class RestaurantRejectedEvent {
    private UUID orderId;
    private UUID restaurantId;
    private UUID sagaId;
    private String rejectionReason;
}
