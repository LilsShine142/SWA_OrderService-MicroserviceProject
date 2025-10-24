package com.example.orderDataaccess.entity;



import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.Objects;
import com.example.orderDomainCore.valueobject.OrderItemId;

/**
 * Lớp này map với bảng "order_items" trong CSDL.
 * Vị trí: order-dataaccess/entity/
 */
@Entity
@Table(name = "order_items", schema = "order")
@IdClass(OrderItemId.class) // Báo cho JPA biết dùng lớp Khóa Composite
public class OrderItemEntity {

    @Id
    private Long id; //

    // Đây là phần thứ 2 của Khóa Composite
    // Vừa là @Id, vừa là Foreign Key (Quan hệ Nhiều-Một)
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id") // Tên cột trong CSDL
    private Order order;

    @Column(name = "product_id")
    private UUID productId; //

    private BigDecimal price; //

    private Integer quantity; //

    @Column(name = "sub_total")
    private BigDecimal subTotal; //

    // Constructor rỗng
    public OrderItemEntity() {}

    // Getters, Setters, equals, hashCode
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
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
        OrderItemEntity that =
