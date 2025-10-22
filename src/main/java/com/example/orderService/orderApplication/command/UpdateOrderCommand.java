package com.example.orderService.orderApplication.command;

import com.example.orderService.orderApplication.dto.request.OrderItemRequestDTO;

import java.util.List;
import java.util.UUID;

public class UpdateOrderCommand {
    private UUID id;
    private String status;
    private String failureMessages;
    private List<OrderItemRequestDTO> items;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getFailureMessages() { return failureMessages; }
    public void setFailureMessages(String failureMessages) { this.failureMessages = failureMessages; }
    public List<OrderItemRequestDTO> getItems() { return items; }
    public void setItems(List<OrderItemRequestDTO> items) { this.items = items; }
}