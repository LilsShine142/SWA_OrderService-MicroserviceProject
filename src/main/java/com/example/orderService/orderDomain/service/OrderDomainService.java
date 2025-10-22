package com.example.orderService.orderDomain.service;

import com.example.orderService.orderDomain.entity.Order;

public interface OrderDomainService {
    void validateOrder(Order order);
}