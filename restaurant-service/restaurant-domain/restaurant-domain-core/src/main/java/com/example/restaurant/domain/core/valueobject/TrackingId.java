package com.example.restaurant.domain.core.valueobject;

import lombok.Value;
import java.util.UUID;

@Value
public class TrackingId {
    UUID value;

    public TrackingId(UUID value) {
        this.value = value;
    }

    public static TrackingId generate() {
        return new TrackingId(UUID.randomUUID());
    }

}