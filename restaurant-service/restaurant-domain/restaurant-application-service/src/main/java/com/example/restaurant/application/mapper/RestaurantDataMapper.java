package com.example.restaurant.application.mapper;

import com.example.restaurant.application.dto.request.ApproveOrderCommand;
import com.example.restaurant.application.dto.response.OrderApprovalResponse;
import com.example.restaurant.domain.core.entity.OrderApproval;
import com.example.restaurant.domain.core.entity.OrderItem;
import com.example.restaurant.domain.core.valueobject.RestaurantId;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RestaurantDataMapper {

    public OrderApproval approveOrderCommandToOrderApproval(ApproveOrderCommand command) {
        List<OrderItem> items = command.getItems().stream()
                .map(dto -> OrderItem.builder()
                        .productId(dto.getProductId())
                        .name("") // Placeholder, assume no name in command
                        .price(dto.getPrice())
                        .quantity(dto.getQuantity())
                        .subTotal(dto.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity())))
                        .build())
                .toList();

        return OrderApproval.builder()
                .orderId(command.getOrderId())
                .restaurantId(RestaurantId.of(command.getRestaurantId()))
                .build();
    }

    public OrderApprovalResponse orderApprovalToResponse(OrderApproval approval, String message) {
        return OrderApprovalResponse.builder()
                .approvalId(approval.getId().getValue())
                .orderId(approval.getOrderId())
//                .trackingId(approval.getTrackingId().getValue())
                .status(approval.getApprovalStatus().name())
                .message(message)
                .build();
    }
}