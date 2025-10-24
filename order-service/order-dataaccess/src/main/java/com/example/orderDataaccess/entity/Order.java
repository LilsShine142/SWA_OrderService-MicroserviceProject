package com.example.orderDataaccess.entity;


import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.Objects;

/**
 * Lớp này map với bảng "orders" trong CSDL .
 * Vị trí: order-dataaccess/entity/
 */
@Entity
@Table(name = "orders", schema = "order")
public class Order {

    @Id
    private UUID id;

    @Column(name = "customer_id")
    private UUID customerId;

    @Column(name = "restaurant_id")
    private UUID restaurantId;

    @Column(name = "tracking_id")
    private UUID trackingId;

    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    private OrderStatus orderStatus;

    @Column(name = "failure_messages")
    private String failureMessages;

    @Column(name = "created_at")
    private Instant createdAt;

    // Quan hệ 1 Order - Nhiều OrderItem
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderItemEntity> items;

    // JPA cần constructor rỗng
    public OrderEntity() {}

    // Getters, Setters, equals, hashCode (Lombok sẽ tự tạo nếu bạn dùng)
    // ... (bạn tự thêm các getter/setter cho các trường trên)

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    // ... (v.v.)

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderEntity that = (OrderEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}