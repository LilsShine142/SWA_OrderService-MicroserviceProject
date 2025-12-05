package com.example.restaurant.domain.core.service;

import com.example.restaurant.domain.core.entity.OrderApproval;
import com.example.restaurant.domain.core.entity.OrderItem;
import com.example.restaurant.domain.core.entity.Restaurant;
import com.example.restaurant.domain.core.event.OrderApprovedEvent;
import com.example.restaurant.domain.core.event.OrderRejectedEvent;

import java.util.List;
import java.util.UUID;

/**
 * Domain Service Implementation cho Order Approval.
 * - Pure Java, không Spring annotations
 * - Không phụ thuộc framework
 * - Chỉ chứa business logic
 */
public class RestaurantDomainServiceImpl implements RestaurantDomainService {

    @Override
    public OrderApprovedEvent approveOrder(Restaurant restaurant, OrderApproval approval, List<OrderItem> items) {
        if (restaurant.checkAvailability(items)) {
            approval.approve();
            return new OrderApprovedEvent(approval);
        } else {
            throw new RuntimeException("Cannot approve: items not available");
        }
    }

    @Override
    public OrderRejectedEvent rejectOrder(Restaurant restaurant, OrderApproval approval, List<OrderItem> items, String reason) {
        approval.reject(reason);
        return new OrderRejectedEvent(approval, reason);
    }
}