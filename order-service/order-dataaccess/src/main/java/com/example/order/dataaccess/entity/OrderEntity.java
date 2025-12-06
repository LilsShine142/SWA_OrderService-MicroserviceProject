package com.example.order.dataaccess.entity;

import com.example.order.domain.core.valueobject.OrderStatus; // Import từ Domain
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.Objects;

/**
 * Lớp này map với bảng "orders" trong CSDL [cite: 499-504].
 * Vị trí: order-dataaccess/entity/
 */
@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    private UUID id; // [cite: 501]

    @Column(name = "customer_id")
    private UUID customerId; // [cite: 501]

    @Column(name = "restaurant_id")
    private UUID restaurantId; // [cite: 501]

    @Column(name = "tracking_id")
    private UUID trackingId; // [cite: 501]

    private BigDecimal price; // [cite: 501]

    @Enumerated(EnumType.STRING) // Map với ENUM 'order_status' [cite: 492]
    @Column(name = "order_status")
    private OrderStatus orderStatus; // [cite: 501]

    @Column(name = "failure_messages")
    private String failureMessages; // [cite: 502]

    @Column(name = "created_at")
    private Instant createdAt; // [cite: 503]

    @Column(name = "street")
    private String street;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "city")
    private String city;

    // Khai báo quan hệ Một-Nhiều (1 Order có nhiều OrderItem)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderItemEntity> items;

    // Constructor rỗng (bắt buộc cho JPA)
    public OrderEntity() {}

    // Getters, Setters (Bạn có thể dùng Lombok)
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public UUID getRestaurantId() { return restaurantId; }
    public void setRestaurantId(UUID restaurantId) { this.restaurantId = restaurantId; }
    public UUID getTrackingId() { return trackingId; }
    public void setTrackingId(UUID trackingId) { this.trackingId = trackingId; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public OrderStatus getOrderStatus() { return orderStatus; }
    public void setOrderStatus(OrderStatus orderStatus) { this.orderStatus = orderStatus; }
    public String getFailureMessages() { return failureMessages; }
    public void setFailureMessages(String failureMessages) { this.failureMessages = failureMessages; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public List<OrderItemEntity> getItems() { return items; }
    public void setItems(List<OrderItemEntity> items) { this.items = items; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

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