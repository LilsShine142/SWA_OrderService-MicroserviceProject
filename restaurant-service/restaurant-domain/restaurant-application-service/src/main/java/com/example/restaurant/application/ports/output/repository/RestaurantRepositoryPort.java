package com.example.restaurant.application.ports.output.repository;

import com.example.restaurant.domain.core.entity.OrderApproval;
import com.example.restaurant.domain.core.entity.Restaurant;
import com.example.restaurant.domain.core.valueobject.RestaurantId;

import java.util.Optional;

public interface RestaurantRepositoryPort {
    Optional<Restaurant> findById(RestaurantId id);
    Restaurant save(Restaurant restaurant);
    void saveApproval(OrderApproval approval);
}