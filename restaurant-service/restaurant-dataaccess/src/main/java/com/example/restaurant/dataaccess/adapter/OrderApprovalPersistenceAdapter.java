package com.example.restaurant.dataaccess.adapter;

import com.example.restaurant.application.ports.output.repository.OrderApprovalRepository;
import com.example.restaurant.dataaccess.entity.OrderApprovalEntity;
import com.example.restaurant.dataaccess.mapper.OrderApprovalDataAccessMapper;
import com.example.restaurant.dataaccess.repository.OrderApprovalJpaRepository;
import com.example.restaurant.domain.core.entity.OrderApproval;
import com.example.restaurant.domain.core.valueobject.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderApprovalPersistenceAdapter implements OrderApprovalRepository {

    private final OrderApprovalJpaRepository orderApprovalJpaRepository;
    private final OrderApprovalDataAccessMapper orderApprovalDataAccessMapper;

    @Override
    public OrderApproval save(OrderApproval approval) {
        OrderApprovalEntity entity = orderApprovalDataAccessMapper.orderApprovalToOrderApprovalEntity(approval);
        OrderApprovalEntity savedEntity = orderApprovalJpaRepository.save(entity);
        return orderApprovalDataAccessMapper.orderApprovalEntityToOrderApproval(savedEntity);
    }

    @Override
    public Optional<OrderApproval> findBySagaId(ApprovalId sagaId) {
        return orderApprovalJpaRepository.findBySagaId(sagaId.getValue())
                .map(orderApprovalDataAccessMapper::orderApprovalEntityToOrderApproval);
    }

    @Override
    public Optional<OrderApproval> findByOrderId(OrderId orderId) {
        return orderApprovalJpaRepository.findByOrderId(orderId.getValue())
                .map(orderApprovalDataAccessMapper::orderApprovalEntityToOrderApproval);
    }
}