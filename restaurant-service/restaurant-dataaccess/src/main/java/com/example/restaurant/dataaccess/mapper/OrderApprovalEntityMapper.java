package com.example.restaurant.dataaccess.mapper;

import com.example.restaurant.dataaccess.entity.OrderApprovalEntity;
import com.example.restaurant.domain.core.entity.OrderApproval;
import com.example.restaurant.domain.core.valueobject.*;
import org.springframework.stereotype.Component;

@Component
public class OrderApprovalEntityMapper {

    public OrderApprovalEntity orderApprovalToOrderApprovalEntity(OrderApproval approval) {
        if (approval == null) return null;

        return OrderApprovalEntity.builder()
                .id(approval.getId() != null ? approval.getId().getValue() : null)
                .orderId(approval.getOrderId())
                .restaurantId(approval.getRestaurantId().getValue())
                .approvalStatus(approval.getApprovalStatus())
                .rejectionReason(approval.getRejectionReason())
                .approvedAt(approval.getApprovedAt())
                .createdAt(approval.getCreatedAt())
                .updatedAt(approval.getUpdatedAt())
                .build();
    }

    public OrderApproval orderApprovalEntityToOrderApproval(OrderApprovalEntity entity) {
        if (entity == null) return null;

        OrderApproval orderApproval = OrderApproval.builder()
                .orderId(new OrderId(entity.getOrderId()).getValue())
                .restaurantId(new RestaurantId(entity.getRestaurantId()))
                .approvalStatus(entity.getApprovalStatus())
                .rejectionReason(entity.getRejectionReason())
                .approvedAt(entity.getApprovedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();

        // Set ID for aggregate root
        if (entity.getId() != null) {
            orderApproval.setId(new ApprovalId(entity.getId()));
        }

        return orderApproval;
    }
}
