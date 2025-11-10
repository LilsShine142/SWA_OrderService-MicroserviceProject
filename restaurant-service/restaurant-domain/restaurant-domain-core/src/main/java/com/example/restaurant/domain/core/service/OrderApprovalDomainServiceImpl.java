package com.example.restaurant.domain.core.service;

import com.example.restaurant.domain.core.entity.OrderApproval;
import com.example.restaurant.domain.core.event.OrderApprovedEvent;
import com.example.restaurant.domain.core.event.OrderRejectedEvent;
import com.example.restaurant.domain.core.exception.RestaurantDomainException;
import com.example.restaurant.domain.core.valueobject.ApprovalId;
import com.example.restaurant.domain.core.valueobject.TrackingId;

import java.util.UUID;

/**
 * Domain Service Implementation cho Order Approval.
 * - Pure Java, không Spring annotations
 * - Không phụ thuộc framework
 * - Chỉ chứa business logic
 */
public class OrderApprovalDomainServiceImpl implements OrderApprovalDomainService {

    @Override
    public OrderApprovedEvent validateAndInitiateApproval(OrderApproval orderApproval) {
        // 1. Validate business rules trước khi phê duyệt
        if (!isValidForApproval(orderApproval)) {
            throw new RestaurantDomainException("Order không hợp lệ để phê duyệt");
        }

        // 2. Xác thực dữ liệu phê duyệt (sử dụng method của entity)
        orderApproval.validateApproval();

        // 3. Khởi tạo quá trình phê duyệt
        orderApproval.initializeApproval(new TrackingId(UUID.randomUUID()), ApprovalId.generate());

        // 4. Phê duyệt đơn hàng
        orderApproval.approve();

        // 5. Trả về sự kiện phê duyệt
        return (OrderApprovedEvent) orderApproval.getDomainEvents().stream()
                .filter(e -> e instanceof OrderApprovedEvent)
                .findFirst()
                .orElseThrow(() -> new RestaurantDomainException("Không tìm thấy OrderApprovedEvent sau khi phê duyệt."));
    }

    @Override
    public OrderRejectedEvent rejectOrderApproval(OrderApproval orderApproval, String rejectionReason) {
        // 1. Validate business rules trước khi từ chối
        validateRejectionBusinessRules(orderApproval, rejectionReason);

        // 2. Xác thực dữ liệu phê duyệt (sử dụng method của entity)
        orderApproval.validateApproval();

        // 3. Từ chối đơn hàng với lý do
        orderApproval.reject(rejectionReason);

        // 4. Trả về sự kiện từ chối
        return (OrderRejectedEvent) orderApproval.getDomainEvents().stream()
                .filter(e -> e instanceof OrderRejectedEvent)
                .findFirst()
                .orElseThrow(() -> new RestaurantDomainException("Không tìm thấy OrderRejectedEvent sau khi từ chối."));
    }

    @Override
    public boolean isValidForApproval(OrderApproval orderApproval) {
        // Business logic: Kiểm tra order có hợp lệ để phê duyệt không
        return hasValidOrderAndRestaurant(orderApproval) &&
                hasValidItems(orderApproval) &&
                isWithinBusinessHours() &&
                !hasDuplicateItems(orderApproval);
    }

    /**
     * Business logic: Kiểm tra order và restaurant có hợp lệ không
     */
    private boolean hasValidOrderAndRestaurant(OrderApproval orderApproval) {
        return orderApproval.getOrderId() != null &&
                orderApproval.getRestaurantId() != null;
    }

    /**
     * Business logic: Kiểm tra items có hợp lệ không
     */
    private boolean hasValidItems(OrderApproval orderApproval) {
        return orderApproval.getItems() != null &&
                !orderApproval.getItems().isEmpty() &&
                orderApproval.getItems().size() <= 20; // Max 20 items
    }

    /**
     * Business logic: Kiểm tra có items trùng lặp không
     */
    private boolean hasDuplicateItems(OrderApproval orderApproval) {
        long distinctCount = orderApproval.getItems().stream()
                .map(item -> item.getProductId().getValue())
                .distinct()
                .count();
        return distinctCount != orderApproval.getItems().size();
    }

    /**
     * Business logic: Validate các quy tắc nghiệp vụ cho việc từ chối
     */
    private void validateRejectionBusinessRules(OrderApproval orderApproval, String rejectionReason) {
        if (rejectionReason == null || rejectionReason.trim().isEmpty()) {
            throw new RestaurantDomainException("Lý do từ chối không được để trống");
        }

        if (rejectionReason.length() > 500) {
            throw new RestaurantDomainException("Lý do từ chối quá dài, tối đa 500 ký tự");
        }

        if (!hasValidOrderAndRestaurant(orderApproval)) {
            throw new RestaurantDomainException("Order hoặc Restaurant không hợp lệ");
        }
    }

    /**
     * Business logic: Kiểm tra có trong giờ làm việc không
     */
    private boolean isWithinBusinessHours() {
        // Giả sử restaurant làm việc từ 8h-22h
        java.time.LocalTime currentTime = java.time.LocalTime.now();
        return !currentTime.isBefore(java.time.LocalTime.of(8, 0)) &&
                !currentTime.isAfter(java.time.LocalTime.of(22, 0));
    }

    /**
     * Business logic: Kiểm tra order có thể được xử lý không
     */
    public boolean canProcessOrder(OrderApproval orderApproval) {
        return hasValidOrderAndRestaurant(orderApproval) &&
                hasValidItems(orderApproval);
    }
}