package com.example.order.domain.core.service;

import com.example.order.domain.core.entity.Order;
import com.example.order.domain.core.event.OrderCreatedEvent;
import com.example.order.domain.core.exception.OrderDomainException;



public class OrderDomainServiceImpl implements OrderDomainService {

    @Override
    public OrderCreatedEvent validateAndInitializeOrder(Order order) {

        order.initializeOrder();

        order.validateOrder();

        return (OrderCreatedEvent) order.getDomainEvents().stream()
                .filter(e -> e instanceof OrderCreatedEvent)
                .findFirst()
                .orElseThrow(() -> new OrderDomainException("Không tìm thấy OrderCreatedEvent sau khi khởi tạo."));
    }
}