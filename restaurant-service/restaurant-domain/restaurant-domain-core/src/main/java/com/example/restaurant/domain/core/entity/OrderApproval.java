package com.example.restaurant.domain.core.entity;

import com.example.restaurant.domain.core.valueobject.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class OrderApproval extends AggregateRoot<ApprovalId> {

    private UUID orderId;
    private RestaurantId restaurantId;
    private ApprovalStatus approvalStatus;
    private String rejectionReason;
    private ZonedDateTime approvedAt;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    public void approve() {
//        if (approvalStatus != ApprovalStatus.PAID) {
//            throw new IllegalStateException("Cannot approve non-pending approval");
//        }
        approvalStatus = ApprovalStatus.APPROVED;
        approvedAt = ZonedDateTime.now();
        updatedAt = ZonedDateTime.now();
    }

    public void reject(String reason) {
//        if (approvalStatus != ApprovalStatus.PAID) {
//            throw new IllegalStateException("Cannot reject non-pending approval");
//        }
        approvalStatus = ApprovalStatus.REJECTED;
        rejectionReason = reason;
        updatedAt = ZonedDateTime.now();
    }
}