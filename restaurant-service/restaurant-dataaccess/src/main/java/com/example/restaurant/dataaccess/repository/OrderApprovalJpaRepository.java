package com.example.restaurant.dataaccess.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.restaurant.dataaccess.entity.OrderApprovalEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderApprovalJpaRepository extends JpaRepository<OrderApprovalEntity, UUID> {

    Optional<OrderApprovalEntity> findBySagaId(UUID sagaId);

    Optional<OrderApprovalEntity> findByOrderId(UUID orderId);

    @Query("SELECT oa FROM OrderApprovalEntity oa WHERE oa.orderId = :orderId AND oa.restaurantId = :restaurantId")
    Optional<OrderApprovalEntity> findByOrderIdAndRestaurantId(@Param("orderId") UUID orderId, @Param("restaurantId") UUID restaurantId);

    List<OrderApprovalEntity> findBySagaStatus(com.example.restaurant.domain.core.valueobject.SagaStatus sagaStatus);

    @Query("SELECT oa FROM OrderApprovalEntity oa WHERE oa.nextRetryAt < CURRENT_TIMESTAMP AND oa.sagaStatus = 'IN_PROGRESS'")
    List<OrderApprovalEntity> findPendingRetries();
}