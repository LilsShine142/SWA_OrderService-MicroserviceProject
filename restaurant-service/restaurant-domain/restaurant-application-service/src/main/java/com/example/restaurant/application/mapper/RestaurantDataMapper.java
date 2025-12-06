package com.example.restaurant.application.mapper;

import com.example.restaurant.application.dto.request.ApproveOrderCommand;
import com.example.restaurant.application.dto.request.RejectOrderCommand;
import com.example.restaurant.application.dto.response.OrderApprovalResponse;
import com.example.restaurant.domain.core.entity.OrderApproval;
import com.example.restaurant.domain.core.entity.OrderItem;
import com.example.restaurant.domain.core.valueobject.ApprovalStatus;
import com.example.restaurant.domain.core.valueobject.RestaurantId;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class RestaurantDataMapper {

    public OrderApproval approveOrderCommandToOrderApproval(ApproveOrderCommand command) {
        List<OrderItem> items = command.getItems().stream()
                .map(dto -> OrderItem.builder()
                        .productId(dto.getProductId())
                        .price(dto.getPrice())
                        .quantity(dto.getQuantity())
                        .subTotal(dto.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity())))
                        .build())
                .toList();

        return OrderApproval.builder()
                .orderId(command.getOrderId())
                .restaurantId(RestaurantId.of(command.getRestaurantId()))
                .approvalStatus(ApprovalStatus.PENDING)  // Tạm thời đặt là PENDING
                .createdAt(ZonedDateTime.now())
                .build();
    }

    public OrderApproval rejectOrderCommandToOrderApproval(RejectOrderCommand command) {
        return OrderApproval.builder()
                .orderId(command.getOrderId())
                .restaurantId(RestaurantId.of(command.getRestaurantId()))
                .approvalStatus(ApprovalStatus.PENDING) // Tạm thời đặt là PENDING
                .createdAt(ZonedDateTime.now())
                .build();
    }

    /**
     * Khi thành công: trả về response đầy đủ + success = true
     */
    public OrderApprovalResponse toSuccessResponse(OrderApproval approval, String message) {
        return OrderApprovalResponse.builder()
                .approvalId(approval.getId() != null ? approval.getId().getValue() : null)
                .orderId(approval.getOrderId())
                .status(approval.getApprovalStatus().name()) // APPROVED hoặc REJECTED
                .message(message)
                .success(true)
                .build();
    }

    /**
     Lỗi nghiệp vụ (duplicate approve/reject, món hết hàng, v.v.): trả về response lỗi + success = false
     */
    public OrderApprovalResponse toFailureResponse(UUID orderId, String errorMessage) {
        return OrderApprovalResponse.builder()
                .orderId(orderId)
                .status("FAILED")
                .message(errorMessage)
                .success(false)
                .build();
    }

    /**
     * Khi lỗi hệ thống bất ngờ
     */
    public OrderApprovalResponse toErrorResponse(UUID orderId, String errorMessage) {
        return OrderApprovalResponse.builder()
                .orderId(orderId)
                .status("ERROR")
                .message(errorMessage)
                .success(false)
                .build();
    }
}