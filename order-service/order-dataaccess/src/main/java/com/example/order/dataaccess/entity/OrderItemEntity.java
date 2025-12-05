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
@Table(name = "order_items", schema = "\"order\"")
@IdClass(OrderItemEntityId.class) // Báo cho JPA biết dùng lớp Khóa Composite
public class OrderItemEntity {

    @Id
    private Long id; // [cite: 507]

    // Đây là phần thứ 2 của Khóa Composite
    // Vừa là @Id, vừa là Foreign Key (Quan hệ Nhiều-Một)
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("order")
    @JoinColumn(name = "order_id") // Tên cột trong CSDL [cite: 508, 511]
    private OrderEntity order;

    @Column(name = "product_id")
    private UUID productId; // [cite: 508]

    private BigDecimal price; // [cite: 508]

    private Integer quantity; // [cite: 509]

    @Column(name = "sub_total")
    private BigDecimal subTotal; //

    // Constructor rỗng (bắt buộc cho JPA)
    public OrderItemEntity() {}

    // Getters, Setters, equals, hashCode (Bạn có thể dùng Lombok)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemEntity that = (OrderItemEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(order, that.order);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, order);
    }
}