package com.example.common_messaging.dto.event;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PaymentCompletedEvent {
    private UUID orderId;
    private UUID paymentId;
    private UUID sagaId;
}
