package com.example.restaurant.application.ports.input.service;

import com.example.common_messaging.dto.event.OrderPaidEvent;
import com.example.restaurant.domain.core.event.PaymentCompletedEvent;
import com.example.restaurant.domain.core.event.PaymentFailedEvent;
import com.example.restaurant.application.dto.request.ApproveOrderCommand;
import com.example.restaurant.application.dto.request.RejectOrderCommand;
import com.example.restaurant.application.dto.response.OrderApprovalResponse;

public interface RestaurantApplicationService {

    void processOrderApproval(PaymentCompletedEvent event);

    /**
     * Xử lý khi thanh toán thất bại từ Payment Service
     */
    void processPaymentFailed(PaymentFailedEvent event);

    OrderApprovalResponse approveOrder(ApproveOrderCommand command);

    OrderApprovalResponse rejectOrder(RejectOrderCommand command);

    // Hàm này để xử lý khi nhận tin nhắn "Order Paid"
    void completeOrderApproval(OrderPaidEvent orderPaidEvent);
}
