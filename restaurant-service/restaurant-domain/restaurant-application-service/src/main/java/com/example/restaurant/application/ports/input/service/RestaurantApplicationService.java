package com.example.restaurant.application.ports.input.service;

import com.example.restaurant.application.dto.request.ApproveOrderCommand;
import com.example.restaurant.application.dto.response.OrderApprovalResponse;
import com.example.restaurant.application.dto.response.MenuItemDto;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;
public interface RestaurantApplicationService {

    /**
     * Use Case: Duyệt đơn hàng từ Order Service (Choreography SAGA)
     * Xử lý:
     * - Kiểm tra món ăn có sẵn không (MenuRepository)
     * - Tạo và validate {@code OrderApproval}
     * - Lưu trạng thái SAGA (saga_id, saga_step, attempt_count)
     * - Publish event: {@code OrderApprovedEvent} hoặc {@code OrderRejectedEvent}
     * </p>
     */
    OrderApprovalResponse approveOrder(@Valid ApproveOrderCommand command);

    void rejectOrder(UUID orderId, String reason);
}
