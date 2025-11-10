package com.example.restaurant.application.ports.output.repository;

import com.example.restaurant.domain.core.entity.MenuItem;
import com.example.restaurant.domain.core.valueobject.ProductId;

import java.util.Optional;

public interface MenuRepository {
    Optional<MenuItem> findByProductId(ProductId productId);
}

