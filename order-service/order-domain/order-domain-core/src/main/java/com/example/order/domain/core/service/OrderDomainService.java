package com.example.order.domain.core.service;


import com.example.order.domain.core.entity.Order;
import com.example.order.domain.core.event.OrderCreatedEvent;

/**
 * Domain Service Interface (Lõi nghiệp vụ).
 * Định nghĩa hành động cần thiết cho việc khởi tạo Order.
 */
public interface OrderDomainService {

    /**
     * Xác thực (validate) Order, khởi tạo các trường giá trị (initialize),
     * và tạo ra sự kiện OrderCreatedEvent.
     * @param order Order Aggregate cần xử lý
     * @return OrderCreatedEvent (Sự kiện đã tạo)
     */
    OrderCreatedEvent validateAndInitializeOrder(Order order);

    // Bạn có thể thêm các hành động khác như:
    // void completeOrderPayment(Order order);
}