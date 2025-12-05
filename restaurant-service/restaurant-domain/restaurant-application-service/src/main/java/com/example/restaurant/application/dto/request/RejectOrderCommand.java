package com.example.restaurant.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class RejectOrderCommand {

    @NotNull(message = "orderId không được null")
    private UUID orderId;

    @NotNull(message = "restaurantId không được null")
    private UUID restaurantId;

    @NotNull(message = "reason không được null")
    private String reason;
}
