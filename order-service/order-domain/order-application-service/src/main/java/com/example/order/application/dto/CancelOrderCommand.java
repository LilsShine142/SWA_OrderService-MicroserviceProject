package com.example.order.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancelOrderCommand {

    @NotNull(message = "Mã theo dõi đơn hàng không được để trống")
    private UUID orderTrackingId;

    @NotNull(message = "Lý do hủy đơn không được để trống")
    private String reason;
}