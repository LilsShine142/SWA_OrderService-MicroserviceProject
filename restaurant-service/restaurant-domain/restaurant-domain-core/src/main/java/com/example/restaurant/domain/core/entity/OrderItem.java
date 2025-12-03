package com.example.restaurant.domain.core.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class OrderItem {
    private UUID productId;
    private String name;
    private BigDecimal price;
    private int quantity;
    private BigDecimal subTotal;
}
