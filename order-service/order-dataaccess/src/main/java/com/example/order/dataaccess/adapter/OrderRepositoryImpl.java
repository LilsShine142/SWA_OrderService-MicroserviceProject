package com.example.order.dataaccess.adapter;


import com.example.order.dataaccess.repository.OrderJpaRepository;
import com.example.order.domain.core.entity.Order;
import com.example.order.domain.core.valueobject.OrderId;
import com.example.order.domain.core.valueobject.TrackingId;
import com.example.order.application.ports.output.OrderRepository;
import com.example.order.dataaccess.entity.OrderEntity;
import com.example.order.dataaccess.mapper.OrderDataaccessMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * ADAPTER PATTERN (Triển khai "Bước 3")
 * Triển khai Output Port 'OrderRepository'
 * Vị trí: order-dataaccess/adapter/
 */
@Component // Báo cho Spring biết đây là Bean
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderDataaccessMapper orderDataAccessMapper;

    // Constructor Injection
    public OrderRepositoryImpl(OrderJpaRepository orderJpaRepository,
                               OrderDataaccessMapper orderDataAccessMapper) {
        this.orderJpaRepository = orderJpaRepository;
        this.orderDataAccessMapper = orderDataAccessMapper;
    }

    @Override
    public Order save(Order order) {
        // 1. Chuyển Domain -> JPA
        OrderEntity orderEntity = orderDataAccessMapper.orderToOrderEntity(order);

        // 2. Lưu bằng Spring Data
        OrderEntity savedEntity = orderJpaRepository.save(orderEntity);

        // 3. Chuyển JPA -> Domain
        return orderDataAccessMapper.orderEntityToOrder(savedEntity);
    }

    @Override
    public Optional<Order> findById(OrderId orderId) {
        return orderJpaRepository
                .findById(orderId.value()) // orderId.value() là UUID
                .map(orderDataAccessMapper::orderEntityToOrder);
    }

    @Override
    public Optional<Order> findByTrackingId(TrackingId trackingId) {
        return orderJpaRepository
                .findByTrackingId(trackingId.value()) // trackingId.value() là UUID
                .map(orderDataAccessMapper::orderEntityToOrder);
    }
}