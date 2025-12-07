package com.example.restaurant.application.ports.output.repository;

import com.example.restaurant.domain.core.entity.OrderApproval;
import com.example.restaurant.domain.core.entity.Restaurant;
import com.example.restaurant.domain.core.valueobject.RestaurantId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RestaurantRepositoryPort {
    Optional<Restaurant> findById(RestaurantId id);
    Restaurant save(Restaurant restaurant);
    void saveApproval(OrderApproval approval);
    OrderApproval save(OrderApproval orderApproval);
    Optional<OrderApproval> findByOrderId(UUID orderId);
    List<Restaurant> findAllRestaurants();
    List<OrderApproval> findAllOrderApprovals();
}