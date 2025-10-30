package com.example.order.application.ports.output;



import com.example.order.domain.core.entity.Order;
import com.example.order.domain.core.valueobject.OrderId;
import com.example.order.domain.core.valueobject.TrackingId;

import java.util.Optional;

/**
 * OUTPUT PORT (Cổng Ra) - Giống hệt "Bước 1" trong ảnh
 * * Interface này định nghĩa các "hợp đồng" mà Lớp Application (Application Layer)
 * CẦN để giao tiếp với CSDL.
 * * Lớp Infrastructure (order-dataaccess) sẽ "implement" (triển khai) interface này.
 * Vị trí: order-application-service/ports/output/repository/
 */
public interface OrderRepository {

    /**
     * Lưu một Order (tạo mới hoặc cập nhật)
     *
     */
    Order save(Order order);

    /**
     * Tìm Order bằng ID
     *
     */
    Optional<Order> findById(OrderId orderId);

    /**
     * Tìm Order bằng Tracking ID (dựa trên CSDL [cite: 522-523] và Listing 2 [cite: 148])
     */
    Optional<Order> findByTrackingId(TrackingId trackingId);
}