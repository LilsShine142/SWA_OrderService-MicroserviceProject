package com.example.orderService.orderApplication.command;

import com.example.orderService.orderApplication.dto.request.OrderItemRequestDTO;

import java.util.List;
import java.util.UUID;

public class CreateOrderCommand {
    private UUID customerId;
    private UUID restaurantId;
    private Double price;
    private List<OrderItemRequestDTO> items;

    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public UUID getRestaurantId() { return restaurantId; }
    public void setRestaurantId(UUID restaurantId) { this.restaurantId = restaurantId; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public List<OrderItemRequestDTO> getItems() { return items; }
    public void setItems(List<OrderItemRequestDTO> items) { this.items = items; }
}