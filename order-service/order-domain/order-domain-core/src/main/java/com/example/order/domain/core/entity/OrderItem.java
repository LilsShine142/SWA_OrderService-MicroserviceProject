package com.example.order.domain.core.entity;


import com.example.order.domain.core.valueobject.*;

/**
 * Entity: OrderItem
 * Đây là một Entity con, nó chỉ "sống" bên trong Order Aggregate.
 * Nó không thể được truy cập trực tiếp từ bên ngoài.
 */
public class OrderItem extends BaseEntity<OrderItemId> {

    // Phải có OrderId để biết nó thuộc về Order nào
    private OrderId orderId;

    private final ProductId productId;
    private final int quantity;
    private final Money price; // Giá tại thời điểm mua
    private final Money subTotal; // Tổng tiền (quantity * price)

    // Hàm khởi tạo riêng tư, chỉ gọi qua Builder
    private OrderItem(Builder builder) {
        this.productId = builder.productId;
        this.quantity = builder.quantity;
        this.price = builder.price;

        // Quy tắc nghiệp vụ: subTotal được tính tự động
        this.subTotal = price.multiply(quantity);
    }

    // Hàm này được gọi bởi Order (cha) khi khởi tạo
    void initializeOrderItem(OrderId orderId, OrderItemId orderItemId) {
        this.orderId = orderId;
        super.setId(orderItemId); // Set ID từ lớp cha BaseEntity
    }

    // Kiểm tra quy tắc nghiệp vụ
    boolean isPriceValid() {
        return price != null &&
                price.isGreaterThan(Money.ZERO) &&
                quantity > 0;
    }

    // Getters (không có setters)
    public OrderId getOrderId() { return orderId; }
    public ProductId getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public Money getPrice() { return price; }
    public Money getSubTotal() { return subTotal; }

    // Builder Pattern
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private OrderItemId id;
        private ProductId productId;
        private int quantity;
        private Money price;

        public Builder id(OrderItemId id) {
            this.id = id;
            return this;
        }

        public Builder productId(ProductId productId) {
            this.productId = productId;
            return this;
        }

        public Builder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder price(Money price) {
            this.price = price;
            return this;
        }

        public OrderItem build() {
            OrderItem orderItem = new OrderItem(this);
            if (id != null) {
                orderItem.setId(id);
            }
            return orderItem;
        }
    }
}