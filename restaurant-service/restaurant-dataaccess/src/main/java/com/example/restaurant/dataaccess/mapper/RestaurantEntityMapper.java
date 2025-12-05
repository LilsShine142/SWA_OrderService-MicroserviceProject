package com.example.restaurant.dataaccess.mapper;

import com.example.restaurant.dataaccess.entity.MenuItemEntity;
import com.example.restaurant.dataaccess.entity.RestaurantEntity;
import com.example.restaurant.domain.core.entity.MenuItem;
import com.example.restaurant.domain.core.entity.Restaurant;
import com.example.restaurant.domain.core.valueobject.RestaurantId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RestaurantEntityMapper {
    private final MenuItemDataAccessMapper menuItemMapper;

    public RestaurantEntityMapper(MenuItemDataAccessMapper menuItemMapper) {
        this.menuItemMapper = menuItemMapper;
    }

    public RestaurantEntity toRestaurantEntity(Restaurant restaurant) {
        RestaurantEntity entity = new RestaurantEntity();
        entity.setId(restaurant.getId().getValue());
        entity.setName(restaurant.getName());
        entity.setAddress(restaurant.getAddress());
        entity.setActive(restaurant.isActive());
        entity.setCreatedAt(restaurant.getCreatedAt());
        entity.setUpdatedAt(restaurant.getUpdatedAt());
        // Menu items are saved separately
        return entity;
    }

    public Restaurant toRestaurant(RestaurantEntity entity) {
        // For now, set menuItems to empty list, as menu items are not loaded in findById
        List<MenuItem> menuItems = List.of();

        return Restaurant.builder()
                .id(new RestaurantId(entity.getId()))
                .name(entity.getName())
                .address(entity.getAddress())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .menuItems(menuItems)
                .build();
    }
}