package com.example.restaurant.domain.core.entity;

import com.example.restaurant.domain.core.event.OrderApprovalCreatedEvent;
import com.example.restaurant.domain.core.event.OrderApprovalRetryEvent;
import com.example.restaurant.domain.core.event.OrderApprovedEvent;
import com.example.restaurant.domain.core.event.OrderRejectedEvent;
import com.example.restaurant.domain.core.valueobject.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class OrderApproval extends AggregateRoot<ApprovalId> {

    private OrderId orderId;
    private RestaurantId restaurantId;
    private TrackingId trackingId;
    private List<MenuItem> items;
    private ApprovalStatus approvalStatus;
    private String rejectionReason;
    private Instant approvedAt;
    private Instant createdAt;
    private Instant updatedAt;

    // === SAGA FIELDS ===
    private ApprovalId sagaId;
    private String sagaStep;
    private SagaStatus sagaStatus;
    private int attemptCount;
    private Instant nextRetryAt;

    // === BUSINESS LOGIC ===

    public void validateApproval() {
        if (items == null || items.isEmpty()) {
            throw new IllegalStateException("Danh sách món ăn không được rỗng");
        }
        if (orderId == null || restaurantId == null) {
            throw new IllegalStateException("OrderId và RestaurantId là bắt buộc");
        }
    }

    public void initializeApproval(TrackingId trackingId, ApprovalId sagaId) {
        setId(ApprovalId.generate());
        this.trackingId = trackingId;
        this.sagaId = sagaId;
        this.sagaStep = "RESTAURANT_APPROVAL";
        this.sagaStatus = SagaStatus.IN_PROGRESS;
        this.attemptCount = 1;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        addDomainEvent(new OrderApprovalCreatedEvent(this));
    }

    public void approve() {
        this.approvalStatus = ApprovalStatus.APPROVED;
        this.approvedAt = Instant.now();
        this.updatedAt = Instant.now();
        this.sagaStatus = SagaStatus.COMPLETED;
        addDomainEvent(new OrderApprovedEvent(this));
    }

    public void reject(String reason) {
        this.approvalStatus = ApprovalStatus.REJECTED;
        this.rejectionReason = reason;
        this.updatedAt = Instant.now();
        this.sagaStatus = SagaStatus.FAILED;
        addDomainEvent(new OrderRejectedEvent(this, reason));
    }

    // === SETTERS (CHO SAGA) ===
    public void setSagaStatus(SagaStatus sagaStatus) {
        this.sagaStatus = sagaStatus;
        this.updatedAt = Instant.now();
    }

    public void setNextRetryAt(Instant nextRetryAt) {
        this.nextRetryAt = nextRetryAt;
        this.updatedAt = Instant.now();
    }

    public void setAttemptCount(int attemptCount) {
        this.attemptCount = attemptCount;
    }
}