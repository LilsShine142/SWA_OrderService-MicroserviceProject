package com.example.orderService.orderContainer.rest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRestRequest {
    @NotNull(message = "Mã khách hàng không được để trống")
    private UUID customerId;

    @NotNull(message = "Mã nhà hàng không được để trống")
    private UUID restaurantId;

    @NotNull(message = "Giá trị đơn hàng không được để trống")
    @DecimalMin(value = "0.0", message = "Giá trị đơn hàng phải lớn hơn hoặc bằng 0")
    private Double price;

    @NotEmpty(message = "Danh sách món ăn không được để trống")
    @Size(min = 1, message = "Đơn hàng phải có ít nhất 1 món")
    private List<@Valid OrderItemRestRequest> items;
}
