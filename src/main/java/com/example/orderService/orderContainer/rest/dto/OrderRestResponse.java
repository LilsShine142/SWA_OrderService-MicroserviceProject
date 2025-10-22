package com.example.orderService.orderContainer.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRestResponse {
    private UUID id;
    private UUID customerId;
    private UUID restaurantId;
    private UUID trackingId;
    private Double price;
    private String status;
    private String failureMessages;
    private LocalDateTime createdAt;
    private List<OrderItemRestResponse> items;
}