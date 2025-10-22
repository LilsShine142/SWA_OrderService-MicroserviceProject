package com.example.orderService.orderInfrastructure.persistence.adapter;

import com.example.orderService.orderDomain.entity.Order;
import com.example.orderService.orderDomain.repository.OrderRepository;
import com.example.orderService.orderInfrastructure.persistence.entity.OrderEntity;
import com.example.orderService.orderInfrastructure.persistence.mapper.OrderPersistenceMapper;
import com.example.orderService.orderInfrastructure.persistence.repository.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepository {
    private final OrderJpaRepository jpaRepository;
    private final OrderPersistenceMapper persistenceMapper;

    @Override
    public Order save(Order order) {
        OrderEntity entity = persistenceMapper.toEntity(order);
        return persistenceMapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return jpaRepository.findById(id).map(persistenceMapper::toDomain);
    }

    @Override
    public List<Order> findAll() {
        return List.of();
    }
}
