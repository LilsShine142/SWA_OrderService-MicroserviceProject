package com.example.common_messaging.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestEvent {
    private String requestId;
    private String id; // customerId or orderId depending on action
    private String action; // CREATE, UPDATE, GET, STATISTICS, APPLY_VOUCHER, RATE, APPROVE
    private Map<String, Object> payload;
}
