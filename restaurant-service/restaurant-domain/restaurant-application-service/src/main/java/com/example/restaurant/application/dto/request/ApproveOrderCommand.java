package com.example.restaurant.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ApproveOrderCommand {

    @NotNull(message = "orderId không được null")
    private UUID orderId;

    @NotNull(message = "restaurantId không được null")
    private UUID restaurantId;

    @NotEmpty(message = "items không được rỗng")
    @Size(min = 1, message = "Phải có ít nhất 1 món")
    private List<@Valid OrderItemDto> items;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDto {
        @NotNull
        private UUID productId;
        @NotNull
        private Integer quantity;
        @NotNull
        private java.math.BigDecimal price;
    }
}