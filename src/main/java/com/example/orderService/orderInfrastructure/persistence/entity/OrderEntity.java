package com.example.orderService.orderInfrastructure.persistence.entity;

import com.example.orderService.orderDomain.valueobject.OrderStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders", schema = "order")
@Data
public class OrderEntity {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "restaurant_id", nullable = false)
    private UUID restaurantId;

    @Column(name = "tracking_id", nullable = false, unique = true)
    private UUID trackingId;

    @Column(name = "price", nullable = false)
    private Double price;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    private OrderStatus status;

    @Column(name = "failure_messages")
    private String failureMessages;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<OrderItemEntity> items;
}
