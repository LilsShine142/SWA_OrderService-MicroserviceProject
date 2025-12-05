package com.example.restaurant.domain.core.event;

import com.example.restaurant.domain.core.entity.OrderApproval;
import lombok.Getter;

@Getter
public class OrderRejectedEvent implements DomainEvent<OrderApproval> {
    private final OrderApproval approval;
    private final String reason;

    public OrderRejectedEvent(OrderApproval approval, String reason) {
        this.approval = approval;
        this.reason = reason;
    }

    @Override
    public OrderApproval getAggregate() { return approval; }
}