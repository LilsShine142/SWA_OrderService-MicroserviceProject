package com.example.order.dataaccess.entity;



import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Lớp này định nghĩa khóa chính tổng hợp (Composite Primary Key)
 * cho OrderItemEntity, khớp với "PRIMARY KEY (id, order_id)".
 * Phải implements Serializable.
 */
public class OrderItemEntityId implements Serializable {

    private Long id; // Khớp với id BIGINT [cite: 507]
    private UUID order; // Khớp với order_id UUID [cite: 508]

    // Constructor rỗng
    public OrderItemEntityId() {}

    // Constructor đầy đủ
    public OrderItemEntityId(Long id, UUID order) {
        this.id = id;
        this.order = order;
    }

    // Getters, Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public UUID getOrder() { return order; }
    public void setOrder(UUID order) { this.order = order; }

    // Bắt buộc phải có equals() và hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemEntityId that = (OrderItemEntityId) o;
        return Objects.equals(id, that.id) && Objects.equals(order, that.order);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, order);
    }
}