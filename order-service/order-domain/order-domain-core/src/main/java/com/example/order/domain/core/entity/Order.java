package com.example.order.domain.core.entity;

import com.example.order.domain.core.event.OrderCreatedEvent;
import com.example.order.domain.core.exception.OrderDomainException;
import com.example.order.domain.core.valueobject.*;


import java.time.Instant; // THÊM: Để dùng cho createdAt
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class Order extends AggregateRoot<OrderId> {

    // --- Các trường bất biến (set 1 lần trong constructor) ---
    private final CustomerId customerId;
    private final RestaurantId restaurantId;
    private final StreetAddress deliveryAddress;
    private final Money price;
    private final List<OrderItem> items;

    // --- Các trường có thể thay đổi (thay đổi qua logic) ---
    private TrackingId trackingId;
    private OrderStatus orderStatus;

    // SỬA 1: Đổi từ List<String> thành String để khớp (VARCHAR)
    private String failureMessages;

    // SỬA 2: Thêm createdAt để khớp (TIMESTAMP)
    private Instant createdAt;

    // Hàm khởi tạo riêng tư, chỉ gọi qua Builder
    private Order(Builder builder) {
        // Gán các giá trị từ Builder
        super.setId(builder.orderId); // Set ID từ lớp cha
        this.customerId = builder.customerId;
        this.restaurantId = builder.restaurantId;
        this.deliveryAddress = builder.deliveryAddress;
        this.price = builder.price;
        this.items = builder.items;
        this.trackingId = builder.trackingId;
        this.orderStatus = builder.orderStatus;
        this.failureMessages = builder.failureMessages;
        this.createdAt = builder.createdAt;
    }

    // --- HÀNH VI & QUY TẮC NGHIỆP VỤ (Business Logic) ---

    public void initializeOrder() {
        validateInitialOrder();
        setId(new OrderId(UUID.randomUUID()));
        trackingId = new TrackingId(UUID.randomUUID());
        orderStatus = OrderStatus.PENDING;

        // SỬA 3: Set thời gian tạo khi khởi tạo
        createdAt = Instant.now();

        initializeOrderItems();
        addDomainEvent(new OrderCreatedEvent(this));
    }

    public void validateOrder() {
        validateTotalPrice();
        validateItemsPrice();
    }

    public void pay() {
        if (orderStatus != OrderStatus.PENDING) {
            throw new OrderDomainException("Chỉ đơn hàng PENDING mới có thể thanh toán!");
        }
        orderStatus = OrderStatus.PAID;
    }

    /**
     * SỬA 4: Phương thức cancel() giờ nhận List
     * nhưng LƯU TRỮ dưới dạng một String duy nhất.
     */
    public void cancel(List<String> failureMessages) {
        if (orderStatus == OrderStatus.PAID || orderStatus == OrderStatus.APPROVED) {
            throw new OrderDomainException("Không thể hủy đơn hàng đã thanh toán hoặc đã duyệt!");
        }
        orderStatus = OrderStatus.CANCELLED;

        // Gộp List<String> thành 1 String (ví dụ: "Lỗi 1, Lỗi 2")
        if (failureMessages != null && !failureMessages.isEmpty()) {
            this.failureMessages = String.join(", ", failureMessages);
        }
    }

    public void approve() {
        if (orderStatus != OrderStatus.PAID) {
            throw new OrderDomainException("Chỉ đơn hàng PAID mới có thể duyệt!");
        }
        orderStatus = OrderStatus.APPROVED;
    }

    public void fail(String reason) {
        if (orderStatus == OrderStatus.APPROVED) {
            throw new OrderDomainException("Không thể thất bại đơn hàng đã duyệt!");
        }
        orderStatus = OrderStatus.CANCELLED;
        this.failureMessages = reason;
    }

    public void reject(String reason) {
        if (orderStatus == OrderStatus.APPROVED) {
            throw new OrderDomainException("Không thể từ chối đơn hàng đã duyệt!");
        }
        orderStatus = OrderStatus.REJECTED;
        this.failureMessages = reason;
    }

    // --- Các quy tắc (Business Rules) nội bộ, dùng private ---

    private void validateTotalPrice() {
        if (price == null || !price.isGreaterThan(Money.ZERO)) {
            throw new OrderDomainException("Tổng tiền phải lớn hơn 0!");
        }
    }

    private void validateInitialOrder() {
        if (orderStatus != null || getId() != null) {
            throw new OrderDomainException("Đơn hàng đã được khởi tạo rồi!");
        }
    }

    private void validateItemsPrice() {
        Money orderItemsTotal = items.stream()
                .map(orderItem -> {
                    validateItemPrice(orderItem);
                    return orderItem.getSubTotal();
                })
                .reduce(Money.ZERO, Money::add);

        if (!price.equals(orderItemsTotal)) {
            throw new OrderDomainException(
                    "Tổng tiền Order: " + price.getAmount() +
                            " không bằng tổng tiền các item: " +
                            orderItemsTotal.getAmount() + "!");
        }
    }

    private void validateItemPrice(OrderItem orderItem) {
        if (!orderItem.isPriceValid()) {
            throw new OrderDomainException(
                    "Giá của item không hợp lệ cho sản phẩm: " +
                            orderItem.getProductId().value());
        }
    }

    private void initializeOrderItems() {
        // Use UUID for unique item IDs
        for (OrderItem orderItem : items) {
            orderItem.initializeOrderItem(this.getId(),
                    new OrderItemId(UUID.randomUUID()));
        }
    }

    // --- Getters (Không có setters) ---
    public CustomerId getCustomerId() { return customerId; }
    public RestaurantId getRestaurantId() { return restaurantId; }
    public StreetAddress getDeliveryAddress() { return deliveryAddress; }
    public Money getPrice() { return price; }
    public List<OrderItem> getItems() { return items; }
    public TrackingId getTrackingId() { return trackingId; }
    public OrderStatus getOrderStatus() { return orderStatus; }

    // SỬA 5: Getter cho các trường đã sửa/thêm
    public String getFailureMessages() { return failureMessages; }
    public Instant getCreatedAt() { return createdAt; }

    // --- Builder Pattern ---
    // Builder được cập nhật để cho phép tạo đối tượng
    // từ CSDL (Hydration)
    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private OrderId orderId;
        private CustomerId customerId;
        private RestaurantId restaurantId;
        private StreetAddress deliveryAddress;
        private Money price;
        private List<OrderItem> items;
        private TrackingId trackingId;
        private OrderStatus orderStatus;
        private String failureMessages;
        private Instant createdAt;

        public Builder orderId(OrderId orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder customerId(CustomerId customerId) {
            this.customerId = customerId;
            return this;
        }

        public Builder restaurantId(RestaurantId restaurantId) {
            this.restaurantId = restaurantId;
            return this;
        }

        public Builder deliveryAddress(StreetAddress deliveryAddress) {
            this.deliveryAddress = deliveryAddress;
            return this;
        }

        public Builder price(Money price) {
            this.price = price;
            return this;
        }

        public Builder items(List<OrderItem> items) {
            this.items = items;
            return this;
        }

        public Builder trackingId(TrackingId trackingId) {
            this.trackingId = trackingId;
            return this;
        }

        public Builder orderStatus(OrderStatus orderStatus) {
            this.orderStatus = orderStatus;
            return this;
        }

        public Builder failureMessages(String failureMessages) {
            this.failureMessages = failureMessages;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Order build() {
            return new Order(this);
        }
    }
}
