package com.example.restaurant.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
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
        @NotNull(message = "ID sản phẩm không được để trống")
        private UUID productId;

        @NotNull(message = "Số lượng không được để trống")
        @Min(value = 1, message = "Số lượng phải ít nhất là 1")
        private Integer quantity;

        @NotNull(message = "Giá sản phẩm không được để trống")
        @DecimalMin(value = "0.01", message = "Giá sản phẩm phải lớn hơn 0")
        private java.math.BigDecimal price;
    }
}