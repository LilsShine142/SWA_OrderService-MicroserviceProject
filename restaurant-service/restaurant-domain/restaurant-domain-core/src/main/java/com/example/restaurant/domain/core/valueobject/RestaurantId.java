package com.example.restaurant.domain.core.valueobject;

import lombok.Value;
import java.util.UUID;

@Value
public class RestaurantId {
    UUID value;

    public RestaurantId(UUID value) {
        this.value = value;
    }

    public static RestaurantId of(UUID value) {
        return new RestaurantId(value);
    }

    public static RestaurantId generate() {
        return new RestaurantId(UUID.randomUUID());
    }
}