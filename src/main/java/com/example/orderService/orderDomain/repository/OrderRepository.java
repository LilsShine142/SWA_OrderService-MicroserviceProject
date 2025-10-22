package com.example.orderService.orderDomain.repository;

import com.example.orderService.orderDomain.entity.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(UUID id);
    List<Order> findAll();
}