package com.example.restaurant.domain.core.valueobject;

import lombok.Value;
import java.util.UUID;

@Value
public class ProductId {
    UUID value;

    public ProductId(UUID value) {
        this.value = value;
    }

    public static ProductId of(UUID value) {
        return new ProductId(value);
    }
}
