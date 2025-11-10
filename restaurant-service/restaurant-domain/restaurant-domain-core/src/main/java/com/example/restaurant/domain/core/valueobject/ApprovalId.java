package com.example.restaurant.domain.core.valueobject;

import lombok.Value;

import java.util.UUID;

@Value
public class ApprovalId {
    UUID value;

    public ApprovalId(UUID value) {
        this.value = value;
    }

    public static ApprovalId of(UUID value) {
        return new ApprovalId(value);
    }

    public static ApprovalId generate() {
        return new ApprovalId(UUID.randomUUID());
    }
}
