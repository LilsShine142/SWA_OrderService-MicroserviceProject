package com.example.orderService.orderApplication.ports;

import com.example.orderService.orderDomain.entity.Order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderApplicationService {
    Order createOrder(Order order);
    Order updateOrder(Order order);
    void deleteOrder(UUID id);
    Optional<Order> findById(UUID id);
    List<Order> findAllOrders();
}