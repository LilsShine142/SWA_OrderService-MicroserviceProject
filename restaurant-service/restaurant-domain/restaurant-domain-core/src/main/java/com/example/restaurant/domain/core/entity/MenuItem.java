package com.example.restaurant.domain.core.entity;

import com.example.restaurant.domain.core.valueobject.*;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZonedDateTime;

@Getter
@Builder
public class MenuItem {
    private final ApprovalId id;
    private final RestaurantId restaurantId;
    private final ProductId productId;
    private final CategoryId categoryId;
    private final String name;
    private final String description;
    private final String imageUrl;
    private final Money price;
    private final boolean available;
    private final ZonedDateTime createdAt;
    private final ZonedDateTime updatedAt;

    public boolean isAvailable() {
        return available && price != null && price.isGreaterThanZero();
    }
}