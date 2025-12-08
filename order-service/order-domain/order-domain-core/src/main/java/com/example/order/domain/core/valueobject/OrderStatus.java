package com.example.order.domain.core.valueobject;


/**
 * Value Object: OrderStatus (dưới dạng Enum)
 * Đại diện cho các trạng thái của một Order.
 */
public enum OrderStatus {
    PENDING, // Đang chờ xử lý
    PAID, // Đã thanh toán
    APPROVED, // Đã duyệt (chờ giao hàng)
    CANCELLING, // Đang trong quá trình hủy (nếu dùng Saga)
    CANCELLED, // Đã hủy
    REJECTED // Đã từ chối
}