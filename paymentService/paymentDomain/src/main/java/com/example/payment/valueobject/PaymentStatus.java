package com.example.payment.valueobject;

/**
 * Enum đại diện cho các trạng thái hợp lệ của thanh toán
 */
public enum PaymentStatus {
    PENDING,        // Thanh toán đang chờ xử lý
    COMPLETED,      // Thanh toán hoàn thành
    FAILED,         // Thanh toán thất bại
    REFUNDED        // Thanh toán đã hoàn tiền
}