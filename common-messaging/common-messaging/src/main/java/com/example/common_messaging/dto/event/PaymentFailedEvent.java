package com.example.common_messaging.dto.event;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class PaymentFailedEvent {
    private UUID orderId;
    private UUID customerId;
    private UUID paymentId;
    private UUID sagaId;
    private BigDecimal amount;
    private String failureReason;
}
