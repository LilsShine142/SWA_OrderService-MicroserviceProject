package com.example.order.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

/**
 * DTO (Query) để theo dõi đơn hàng.
 * Chuyển từ Record sang Class để dùng Lombok và Message Validation.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackOrderQuery {

    @NotNull(message = "Mã theo dõi đơn hàng không được để trống")
    private UUID orderTrackingId;
}