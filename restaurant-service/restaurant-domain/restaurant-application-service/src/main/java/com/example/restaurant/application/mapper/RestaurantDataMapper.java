package com.example.restaurant.application.mapper;

import com.example.restaurant.application.dto.request.ApproveOrderCommand;
import com.example.restaurant.application.dto.response.OrderApprovalResponse;
import com.example.restaurant.domain.core.entity.MenuItem;
import com.example.restaurant.domain.core.entity.OrderApproval;
import com.example.restaurant.domain.core.valueobject.Money;
import com.example.restaurant.domain.core.valueobject.OrderId;
import com.example.restaurant.domain.core.valueobject.ProductId;
import com.example.restaurant.domain.core.valueobject.RestaurantId;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RestaurantDataMapper {

    public OrderApproval approveOrderCommandToOrderApproval(ApproveOrderCommand command) {
        List<MenuItem> items = command.getItems().stream()
                .map(dto -> MenuItem.builder()
                        .productId(ProductId.of(dto.getProductId()))
//                        .quantity(dto.getQuantity())
                        .price(new Money(dto.getPrice()))
                        .available(true) // Sẽ validate từ DB
                        .build())
                .collect(Collectors.toList());

        return OrderApproval.builder()
                .orderId(OrderId.of(command.getOrderId()))
                .restaurantId(RestaurantId.of(command.getRestaurantId()))
                .items(items)
                .build();
    }

    public OrderApprovalResponse orderApprovalToResponse(OrderApproval approval, String message) {
        return OrderApprovalResponse.builder()
                .approvalId(approval.getId().getValue())
                .orderId(approval.getOrderId().getValue())
                .trackingId(approval.getTrackingId().getValue())
                .status(approval.getApprovalStatus().name())
                .message(message)
                .build();
    }
}