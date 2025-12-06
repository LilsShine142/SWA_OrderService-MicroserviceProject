package com.example.restaurant.application.dto.response;

import lombok.*;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderApprovalResponse {
    private UUID approvalId;
    private UUID orderId;
//    private UUID trackingId;
    private String status; // APPROVED / REJECTED
    private String message;
    private boolean success;
}
