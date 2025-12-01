package com.example.restaurant.dataaccess.entity;

import com.example.restaurant.domain.core.valueobject.ApprovalStatus;
import com.example.restaurant.domain.core.valueobject.SagaStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;


@Entity
@Table(name = "order_approvals", schema = "\"restaurant\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderApprovalEntity {

    @Id
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "order_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID orderId;

    @Column(name = "restaurant_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID restaurantId;

    @Column(name = "tracking_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID trackingId;

    @Column(name = "saga_id", columnDefinition = "BINARY(16)")
    private UUID sagaId;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status", nullable = false, length = 20)
    private ApprovalStatus approvalStatus;

    @Column(name = "rejection_reason")
    private String rejectionReason;

    @Column(name = "saga_step", length = 50)
    private String sagaStep;

    @Enumerated(EnumType.STRING)
    @Column(name = "saga_status", length = 20)
    private SagaStatus sagaStatus;

    @Column(name = "attempt_count")
    private Integer attemptCount;

    @Column(name = "approved_at")
    private OffsetDateTime approvedAt;

    @Column(name = "next_retry_at")
    private OffsetDateTime nextRetryAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = OffsetDateTime.now();
        }
        if (this.attemptCount == null) {
            this.attemptCount = 0;
        }
        if (this.sagaStatus == null) {
            this.sagaStatus = SagaStatus.STARTED;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}