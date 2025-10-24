package com.example.order.dataaccess.repository;


import com.example.order.dataaccess.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Interface Spring Data JPA tự động tạo câu lệnh SQL.
 * Vị trí: order-dataaccess/repository/
 */
@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {

    // Tự động tạo câu lệnh "SELECT * FROM orders WHERE tracking_id = ?"
    // [cite: 394, 522]
    Optional<OrderEntity> findByTrackingId(UUID trackingId);
}