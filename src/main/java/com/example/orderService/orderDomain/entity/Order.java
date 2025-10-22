package com.example.orderService.orderDomain.entity;

import com.example.orderService.orderDomain.valueobject.OrderItem;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Order {
    private UUID id;
    private UUID customerId;
    private UUID restaurantId;
    private UUID trackingId;
    private Double price;
    private String status;
    private String failureMessages;
    private LocalDateTime createdAt;
    private List<OrderItem> items;

    public boolean isValid() {
        return id != null && customerId != null && restaurantId != null && trackingId != null &&
                price != null && status != null && !status.isEmpty() && createdAt != null && items != null && !items.isEmpty();
    }

    // Getters và setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public UUID getRestaurantId() { return restaurantId; }
    public void setRestaurantId(UUID restaurantId) { this.restaurantId = restaurantId; }
    public UUID getTrackingId() { return trackingId; }
    public void setTrackingId(UUID trackingId) { this.trackingId = trackingId; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getFailureMessages() { return failureMessages; }
    public void setFailureMessages(String failureMessages) { this.failureMessages = failureMessages; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }
}