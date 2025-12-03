package com.example.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Event received from Order Service
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent implements Serializable {
    private UUID orderId;
    private UUID customerId;
    private BigDecimal price;
    private String status; // CREATED, CANCELLED, COMPLETED
}
