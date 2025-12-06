package com.example.order.application.mapper;


import com.example.order.application.dto.CancelOrderResponse;
import com.example.order.application.dto.CreateOrderCommand;
import com.example.order.domain.core.entity.Order;
import com.example.order.domain.core.entity.OrderItem;
import com.example.order.domain.core.valueobject.*;
import com.example.order.application.dto.CreateOrderResponse;
import com.example.order.application.dto.TrackOrderResponse;
import com.example.order.application.dto.UpdateOrderResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Mapper: Chuyển đổi DTOs (Data Transfer Objects)
 * sang Domain Entities và ngược lại.
 */
@Component
public class OrderDataMapper {

    /**
     * Chuyển DTO (Command) -> Domain Entity (Order)
     * Dùng để truyền dữ liệu vào Domain
     */
    public Order createOrderCommandToOrder(CreateOrderCommand command) {

        // 1. Chuyển Address DTO -> StreetAddress (Value Object)
        // (Giả định Order.java có trường deliveryAddress
        //  theo Listing 6 [cite: 411] và DTO [cite: 205-209])
        StreetAddress address = new StreetAddress(
                UUID.randomUUID(), // ID tạm thời cho địa chỉ
                command.address().street(),
                command.address().postalCode(),
                command.address().city()
        );

        // 2. Chuyển List<OrderItemCommand> (DTO) -> List<OrderItem> (Entity)
        List<OrderItem> items = command.items().stream()
                .map(itemCmd -> OrderItem.builder()
                        .productId(new ProductId(itemCmd.productId()))
                        .quantity(itemCmd.quantity())
                        .price(new Money(itemCmd.price()))
                        // .subTotal() sẽ được tính toán tự động
                        // bên trong Domain Entity
                        .build())
                .collect(Collectors.toList());

        // Tính tổng tiền từ các item, CÓ THỂ BỎ QUA NẾU DOMAIN XỬ LÝ
        BigDecimal totalPrice = items.stream()
                .map(item -> item.getSubTotal().getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. Dùng Builder của Order (từ Domain) để tạo đối tượng
        return Order.builder()
                .customerId(new CustomerId(command.customerId()))
                .restaurantId(new RestaurantId(command.restaurantId()))
                .price(new Money(totalPrice))
                .deliveryAddress(address)
                .items(items)
                .build();
    }

    /**
     * Chuyển Domain Entity (Order) -> DTO (CreateOrderResponse)
     * Dùng để trả dữ liệu ra cho Controller
     */
    public CreateOrderResponse orderToCreateOrderResponse(Order order, String message) {
        return new CreateOrderResponse(
                order.getTrackingId().value(),
                order.getOrderStatus(),
                message
        );
    }

    /**
     * Chuyển Domain Entity (Order) -> DTO (TrackOrderResponse)
     */
    public TrackOrderResponse orderToTrackOrderResponse(Order order) {
        return new TrackOrderResponse(
                order.getTrackingId().value(),
                order.getOrderStatus(),
                Collections.singletonList(order.getFailureMessages()) // Chuyển đổi List<String> sang String nếu cần
        );
    }

    /**
     * Chuyển Domain Entity (Order) -> DTO (CancelOrderResponse)
     */
    public CancelOrderResponse orderToCancelOrderResponse(Order order, String message) {
        return new CancelOrderResponse(
                order.getOrderStatus(),
                message
        );
    }

    /**
     * Chuyển Domain Entity (Order) -> DTO (UpdateOrderResponse)
     */
    public UpdateOrderResponse orderToUpdateOrderResponse(Order order, String message) {
        return new UpdateOrderResponse(
                order.getTrackingId().value(),
                order.getOrderStatus(),
                message
        );
    }
}