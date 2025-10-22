package com.example.orderService.orderContainer.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRestResponse {
    private Long id;
    private UUID productId;
    private Double price;
    private Integer quantity;
    private Double subTotal;
}