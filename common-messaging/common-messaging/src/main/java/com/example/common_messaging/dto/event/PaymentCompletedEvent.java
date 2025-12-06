package com.example.common_messaging.dto.event;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCompletedEvent {
    private UUID orderId;
    private UUID paymentId;
    private UUID customerId;
    private BigDecimal amount;
    private Instant createdAt;
}
