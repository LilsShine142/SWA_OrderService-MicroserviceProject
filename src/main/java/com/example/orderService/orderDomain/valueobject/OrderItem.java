package com.example.orderService.orderDomain.valueobject;

import java.util.UUID;

public class OrderItem {
    private Long id;
    private UUID orderId;
    private UUID productId;
    private Double price;
    private Integer quantity;
    private Double subTotal;

    public OrderItem(UUID productId, Double price, Integer quantity, Double subTotal) {
        this.productId = productId;
        this.price = price;
        this.quantity = quantity;
        this.subTotal = subTotal;
    }

    // Getters và setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public UUID getOrderId() { return orderId; }
    public void setOrderId(UUID orderId) { this.orderId = orderId; }
    public UUID getProductId() { return productId; }
    public void setProductId(UUID productId) { this.productId = productId; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public Double getSubTotal() { return subTotal; }
    public void setSubTotal(Double subTotal) { this.subTotal = subTotal; }
}