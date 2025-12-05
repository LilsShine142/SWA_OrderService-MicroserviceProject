package com.example.restaurant.domain.core.entity;

import com.example.restaurant.domain.core.entity.BaseEntity;
import com.example.restaurant.domain.core.event.DomainEvent;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public abstract class AggregateRoot<T> extends BaseEntity<T> {

    private final transient List<DomainEvent<?>> domainEvents = new ArrayList<>();

    protected void addDomainEvent(DomainEvent<?> event) {
        this.domainEvents.add(event);
    }

    public List<DomainEvent<?>> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    public void clearDomainEvents() {
        this.domainEvents.clear();
    }
}