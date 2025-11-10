package com.example.restaurant.application.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemDto {
    private UUID id;
    private UUID productId;
    private String name;
    private BigDecimal price;
    private boolean available;
}