package com.example.common_messaging.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor // <--- QUAN TRỌNG: Để Jackson khởi tạo object
@AllArgsConstructor
public class PaymentProcessedEvent {
    private UUID orderId;
    private UUID customerId;
    private UUID paymentId;
    private UUID sagaId;
    private BigDecimal amount;
}
