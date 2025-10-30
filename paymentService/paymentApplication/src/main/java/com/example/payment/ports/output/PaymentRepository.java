package com.example.payment.ports.output;


import com.example.payment.entity.Payment;
import com.example.payment.valueobject.OrderId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * OUTPUT PORT (Cổng Ra)
 * Interface này định nghĩa các "hợp đồng" mà Lớp Application (Application Layer)
 * CẦN để giao tiếp với CSDL.
 * Lớp Infrastructure sẽ "implement" (triển khai) interface này.
 */
public interface PaymentRepository {

    /**
     * Lưu một Payment (tạo mới hoặc cập nhật)
     * @param payment Payment entity
     * @return Payment đã được lưu
     */
    Payment save(Payment payment);

    /**
     * Cập nhật Payment (dùng cho SAGA step updates, status changes)
     */
    Payment update(Payment payment);

    /**
     * Tìm Payment bằng Payment ID
     * @param paymentId ID của payment
     * @return Optional chứa Payment nếu tìm thấy
     */
    Optional<Payment> findById(UUID paymentId);

    /**
     * Tìm Payment bằng Order ID
     * @param orderId ID của order
     * @return Optional chứa Payment nếu tìm thấy
     */
    Optional<Payment> findByOrderId(OrderId orderId);

    /**
     * Tìm tất cả Payments theo trạng thái
     * @param status Trạng thái payment (PENDING, COMPLETED, FAILED, REFUNDED)
     * @return List các payments theo trạng thái
     */
    // List<Payment> findByStatus(PaymentStatus status);

    /**
     * Tìm tất cả Payments theo Saga ID (cho SAGA orchestration)
     */
    List<Payment> findBySagaId(String sagaId);
}