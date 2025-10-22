package com.example.orderService.orderApplication.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponseDTO {
    private Long id;
    private UUID productId;
    private Double price;
    private Integer quantity;
    private Double subTotal;
}