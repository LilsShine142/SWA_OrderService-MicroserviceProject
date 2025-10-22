package com.example.orderService.orderInfrastructure.persistence.repository;

import com.example.orderService.orderInfrastructure.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {
}