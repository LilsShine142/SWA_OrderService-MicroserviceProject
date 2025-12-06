package com.example.order.dataaccess.adapter;


import com.example.order.dataaccess.repository.OrderJpaRepository;
import com.example.order.domain.core.entity.Order;
import com.example.order.domain.core.valueobject.TrackingId;
import com.example.order.application.ports.output.OrderRepository;
import com.example.order.dataaccess.entity.OrderEntity;
import com.example.order.dataaccess.mapper.OrderDataaccessMapper;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * ADAPTER PATTERN (Triển khai "Bước 3")
 * Triển khai Output Port 'OrderRepository'
 * Vị trí: order-dataaccess/adapter/
 */
@Repository // Báo cho Spring biết đây là Bean
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
    public Optional<Order> findById(UUID orderId) {
        return orderJpaRepository
                .findById(orderId)
                .map(orderDataAccessMapper::orderEntityToOrder);
    }

    @Override
    public Optional<Order> findByTrackingId(TrackingId trackingId) {
        return orderJpaRepository
                .findByTrackingId(trackingId.value()) // trackingId.value() là UUID
                .map(orderDataAccessMapper::orderEntityToOrder);
    }

    @Override
    public List<Order> findAll() {
        return orderJpaRepository.findAll().stream()
                .map(orderDataAccessMapper::orderEntityToOrder) // Chuyển Entity JPA -> Domain Order
                .collect(Collectors.toList());
    }
}