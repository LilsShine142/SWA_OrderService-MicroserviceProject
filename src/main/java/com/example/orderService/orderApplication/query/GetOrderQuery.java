package com.example.orderService.orderApplication.query;

import java.util.UUID;

public class GetOrderQuery {
    private UUID id;

    public GetOrderQuery(UUID id) {
        this.id = id;
    }

    public UUID getId() { return id; }
}