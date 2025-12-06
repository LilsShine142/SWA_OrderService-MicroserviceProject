package com.example.restaurant.domain.core.service;

import com.example.restaurant.domain.core.entity.OrderApproval;
import com.example.restaurant.domain.core.entity.OrderItem;
import com.example.restaurant.domain.core.entity.Restaurant;
import com.example.restaurant.domain.core.event.OrderApprovedEvent;
import com.example.restaurant.domain.core.event.OrderRejectedEvent;

import java.util.List;

/**
 * Domain Service Implementation – Pure business logic
 */
public class RestaurantDomainServiceImpl implements RestaurantDomainService {

    @Override
    public OrderApprovedEvent approveOrder(Restaurant restaurant, OrderApproval approval, List<OrderItem> items) {
        // Kiểm tra món có sẵn không
        if (!restaurant.checkAvailability(items)) {
            throw new IllegalStateException("Một hoặc nhiều món trong đơn đã hết hàng hoặc không tồn tại");
        }

        // Nếu OK → duyệt đơn
        approval.approve(); // ← chỉ thay đổi trạng thái ở đây
        return new OrderApprovedEvent(approval);
    }

    @Override
    public OrderRejectedEvent rejectOrder(Restaurant restaurant, OrderApproval approval, List<OrderItem> items, String reason) {
        approval.reject(reason); // ← chỉ thay đổi trạng thái ở đây
        return new OrderRejectedEvent(approval, reason);
    }
}