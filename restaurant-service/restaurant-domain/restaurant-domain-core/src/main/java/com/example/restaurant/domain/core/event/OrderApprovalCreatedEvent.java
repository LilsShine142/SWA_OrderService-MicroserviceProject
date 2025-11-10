package com.example.restaurant.domain.core.event;

import com.example.restaurant.domain.core.entity.OrderApproval;

public class OrderApprovalCreatedEvent implements DomainEvent<OrderApproval> {
    private final OrderApproval approval;

    public OrderApprovalCreatedEvent(OrderApproval approval) {
        this.approval = approval;
    }

    @Override
    public OrderApproval getAggregate() { return approval; }
}