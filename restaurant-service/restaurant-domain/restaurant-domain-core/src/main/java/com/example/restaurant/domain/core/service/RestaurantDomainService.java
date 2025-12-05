package com.example.restaurant.domain.core.service;

import com.example.restaurant.domain.core.entity.OrderApproval;
import com.example.restaurant.domain.core.entity.OrderItem;
import com.example.restaurant.domain.core.entity.Restaurant;
import com.example.restaurant.domain.core.event.OrderApprovedEvent;
import com.example.restaurant.domain.core.event.OrderRejectedEvent;

import java.util.List;

/**
 * Domain Service Interface cho Restaurant Service.
 */
public interface RestaurantDomainService {
    OrderApprovedEvent approveOrder(Restaurant restaurant, OrderApproval approval, List<OrderItem> items);
    OrderRejectedEvent rejectOrder(Restaurant restaurant, OrderApproval approval, List<OrderItem> items, String reason);
}

