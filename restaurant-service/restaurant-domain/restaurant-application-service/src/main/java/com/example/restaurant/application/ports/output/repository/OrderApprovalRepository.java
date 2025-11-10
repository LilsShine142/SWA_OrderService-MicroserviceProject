package com.example.restaurant.application.ports.output.repository;

import com.example.restaurant.domain.core.entity.OrderApproval;
import com.example.restaurant.domain.core.valueobject.ApprovalId;
import com.example.restaurant.domain.core.valueobject.OrderId;

import java.util.Optional;

public interface OrderApprovalRepository {
    OrderApproval save(OrderApproval approval);
    Optional<OrderApproval> findBySagaId(ApprovalId sagaId);
    Optional<OrderApproval> findByOrderId(OrderId orderId); // THÊM METHOD NÀY
}