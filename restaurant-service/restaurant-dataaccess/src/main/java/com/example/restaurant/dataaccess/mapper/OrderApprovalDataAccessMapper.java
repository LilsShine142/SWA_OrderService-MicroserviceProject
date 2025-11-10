package com.example.restaurant.dataaccess.mapper;

import com.example.restaurant.dataaccess.entity.OrderApprovalEntity;
import com.example.restaurant.domain.core.entity.OrderApproval;
import com.example.restaurant.domain.core.valueobject.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.stream.Collectors;

@Component
public class OrderApprovalDataAccessMapper {

    public OrderApprovalEntity orderApprovalToOrderApprovalEntity(OrderApproval approval) {
        if (approval == null) return null;

        return OrderApprovalEntity.builder()
                .id(approval.getId() != null ? approval.getId().getValue() : null)
                .orderId(approval.getOrderId().getValue())
                .restaurantId(approval.getRestaurantId().getValue())
                .trackingId(approval.getTrackingId().getValue())
                .sagaId(approval.getSagaId().getValue())
                .approvalStatus(approval.getApprovalStatus())
                .rejectionReason(approval.getRejectionReason())
                .sagaStep(approval.getSagaStep())
                .sagaStatus(approval.getSagaStatus())
                .attemptCount(approval.getAttemptCount())
                .approvedAt(toOffsetDateTime(approval.getApprovedAt()))
                .nextRetryAt(toOffsetDateTime(approval.getNextRetryAt()))
                .createdAt(toOffsetDateTime(approval.getCreatedAt()))
                .updatedAt(toOffsetDateTime(approval.getUpdatedAt()))
                .build();
    }

    public OrderApproval orderApprovalEntityToOrderApproval(OrderApprovalEntity entity) {
        if (entity == null) return null;

        OrderApproval orderApproval = OrderApproval.builder()
                .orderId(new OrderId(entity.getOrderId()))
                .restaurantId(new RestaurantId(entity.getRestaurantId()))
                .trackingId(new TrackingId(entity.getTrackingId()))
                .sagaId(new ApprovalId(entity.getSagaId()))
                .approvalStatus(entity.getApprovalStatus())
                .rejectionReason(entity.getRejectionReason())
                .sagaStep(entity.getSagaStep())
                .sagaStatus(entity.getSagaStatus())
                .attemptCount(entity.getAttemptCount())
                .approvedAt(toInstant(entity.getApprovedAt()))
                .nextRetryAt(toInstant(entity.getNextRetryAt()))
                .createdAt(toInstant(entity.getCreatedAt()))
                .updatedAt(toInstant(entity.getUpdatedAt()))
                .build();

        // Set ID for aggregate root
        if (entity.getId() != null) {
            orderApproval.setId(new ApprovalId(entity.getId()));
        }

        return orderApproval;
    }

    private Instant toInstant(OffsetDateTime offsetDateTime) {
        return offsetDateTime != null ? offsetDateTime.toInstant() : null;
    }

    private OffsetDateTime toOffsetDateTime(Instant instant) {
        return instant != null ? OffsetDateTime.ofInstant(instant, ZoneOffset.UTC) : null;
    }
}
