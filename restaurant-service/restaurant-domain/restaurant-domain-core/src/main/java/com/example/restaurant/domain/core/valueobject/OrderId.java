package com.example.restaurant.domain.core.valueobject;

import lombok.Value;
import java.util.UUID;

@Value
public class OrderId {
    UUID value;

    public OrderId(UUID value) {
        this.value = value;
    }

    public static OrderId of(UUID value) {
        return new OrderId(value);
    }

    public static OrderId generate() {
        return new OrderId(UUID.randomUUID());
    }
}
