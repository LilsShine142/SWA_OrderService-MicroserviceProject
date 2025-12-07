package com.example.order.dataaccess.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.Objects;

/**
 * Lớp này map với bảng "order_items" trong CSDL [cite: 506-512].
 * Vị trí: order-dataaccess/entity/
 */
@Entity
@Table(name = "order_items")
@IdClass(OrderItemEntityId.class)
public class OrderItemEntity {

    @Id
    @Column(name = "id")
    private UUID id;

    @Id
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    @Column(name = "product_id")
    private UUID productId;

    private BigDecimal price;

    private Integer quantity;

    @Column(name = "sub_total")
    private BigDecimal subTotal;

    // Constructor rỗng (bắt buộc cho JPA)
    public OrderItemEntity() {}

    // Getters, Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public OrderEntity getOrder() { return order; }
    public void setOrder(OrderEntity order) { this.order = order; }
    public UUID getProductId() { return productId; }
    public void setProductId(UUID productId) { this.productId = productId; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getSubTotal() { return subTotal; }
    public void setSubTotal(BigDecimal subTotal) { this.subTotal = subTotal; }

    // equals, hashCode dựa trên id
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderItemEntity)) return false;
        OrderItemEntity that = (OrderItemEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}