package com.example.restaurant.domain.core.valueobject;

import lombok.Value;
import java.util.UUID;

@Value
public class OrderId {
    UUID value;

    public OrderId(UUID value) {
        this.value = value;
    }

    public static UUID of(UUID value) {
        return new OrderId(value).getValue();
    }

    public static OrderId generate() {
        return new OrderId(UUID.randomUUID());
    }
}
