package com.example.restaurant.domain.core.event;

public interface DomainEvent<T> {
    T getAggregate();
}
