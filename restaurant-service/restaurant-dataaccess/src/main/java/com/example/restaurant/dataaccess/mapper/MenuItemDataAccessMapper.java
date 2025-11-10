package com.example.restaurant.dataaccess.mapper;

import com.example.restaurant.dataaccess.entity.MenuItemEntity;
import com.example.restaurant.domain.core.entity.MenuItem;
import com.example.restaurant.domain.core.valueobject.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
public class MenuItemDataAccessMapper {

    public MenuItem menuItemEntityToMenuItem(MenuItemEntity entity) {
        if (entity == null) return null;

        return MenuItem.builder()
                .id(new ApprovalId(entity.getId()))
                .restaurantId(new RestaurantId(entity.getRestaurantId()))
                .productId(new ProductId(entity.getProductId()))
                .categoryId(new CategoryId(entity.getCategoryId()))
                .name(entity.getName())
                .description(entity.getDescription())
                .imageUrl(entity.getImageUrl())
                .price(new Money(entity.getPrice()))
                .available(entity.getAvailable())
                .createdAt(toInstant(entity.getCreatedAt()))
                .updatedAt(toInstant(entity.getUpdatedAt()))
                .build();
    }

    public MenuItemEntity menuItemToMenuEntity(MenuItem menuItem) {
        if (menuItem == null) return null;

        return MenuItemEntity.builder()
                .id(menuItem.getId() != null ? menuItem.getId().getValue() : null)
                .restaurantId(menuItem.getRestaurantId().getValue())
                .productId(menuItem.getProductId().getValue())
                .categoryId(menuItem.getCategoryId().getValue())
                .name(menuItem.getName())
                .description(menuItem.getDescription())
                .imageUrl(menuItem.getImageUrl())
                .price(menuItem.getPrice().getAmount())
                .available(menuItem.isAvailable())
                .createdAt(toOffsetDateTime(menuItem.getCreatedAt()))
                .updatedAt(toOffsetDateTime(menuItem.getUpdatedAt()))
                .build();
    }

    private Instant toInstant(OffsetDateTime offsetDateTime) {
        return offsetDateTime != null ? offsetDateTime.toInstant() : null;
    }

    private OffsetDateTime toOffsetDateTime(Instant instant) {
        return instant != null ? OffsetDateTime.ofInstant(instant, ZoneOffset.UTC) : null;
    }
}