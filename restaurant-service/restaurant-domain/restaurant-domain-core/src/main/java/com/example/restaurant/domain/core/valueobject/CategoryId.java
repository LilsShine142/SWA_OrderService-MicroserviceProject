package com.example.restaurant.domain.core.valueobject;

import lombok.Value;
import java.util.UUID;

@Value
public class CategoryId {
    UUID value;
    public CategoryId(UUID v) {
        this.value = v;
    }
    public static CategoryId of(UUID v) {
        return new CategoryId(v);
    }
}
