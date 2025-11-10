package com.example.restaurant.domain.core.service;

import com.example.restaurant.domain.core.entity.OrderApproval;
import com.example.restaurant.domain.core.event.OrderApprovedEvent;
import com.example.restaurant.domain.core.event.OrderRejectedEvent;

/**
 * Domain Service Interface cho Restaurant Service.
 */
public interface OrderApprovalDomainService {

    /**
     * Xác thực và khởi tạo quá trình phê duyệt đơn hàng
     * @param orderApproval OrderApproval Aggregate cần xử lý
     * @return OrderApprovedEvent (Sự kiện đã phê duyệt)
     */
    OrderApprovedEvent validateAndInitiateApproval(OrderApproval orderApproval);

    /**
     * Từ chối phê duyệt đơn hàng
     * @param orderApproval OrderApproval Aggregate cần xử lý
     * @param rejectionReason Lý do từ chối
     * @return OrderRejectedEvent (Sự kiện đã từ chối)
     */
    OrderRejectedEvent rejectOrderApproval(OrderApproval orderApproval, String rejectionReason);

    /**
     * Kiểm tra tính hợp lệ của order để phê duyệt (business rules)
     * @param orderApproval OrderApproval cần kiểm tra
     * @return boolean - true nếu order hợp lệ để phê duyệt
     */
    boolean isValidForApproval(OrderApproval orderApproval);
}

