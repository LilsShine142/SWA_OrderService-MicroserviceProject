package com.example.payment.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

// DTO này chứa dữ liệu y hệt như OrderCreatedEvent
@Data
public class SimulateOrderCreatedRequest {
    private UUID orderId;
    private UUID customerId;
    private BigDecimal totalAmount;
}