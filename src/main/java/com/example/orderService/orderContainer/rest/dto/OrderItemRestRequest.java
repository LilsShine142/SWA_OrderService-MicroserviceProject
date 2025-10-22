package com.example.orderService.orderContainer.rest.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRestRequest {
    @NotNull(message = "Mã sản phẩm không được để trống")
    private UUID productId;

    @NotNull(message = "Giá sản phẩm không được để trống")
    @DecimalMin(value = "0.0", message = "Giá sản phẩm phải lớn hơn hoặc bằng 0")
    private Double price;

    @Min(value = 1, message = "Số lượng phải lớn hơn 0")
    private Integer quantity;

    @NotNull(message = "Tổng phụ không được để trống")
    @DecimalMin(value = "0.0", message = "Tổng phụ phải lớn hơn hoặc bằng 0")
    private Double subTotal;
}
