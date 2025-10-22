package com.example.orderService.orderApplication.mapper;


import com.example.orderService.orderApplication.dto.request.CreateOrderRequestDTO;
import com.example.orderService.orderApplication.dto.request.OrderItemRequestDTO;
import com.example.orderService.orderApplication.dto.response.OrderResponseDTO;
import com.example.orderService.orderDomain.entity.Order;
import com.example.orderService.orderDomain.valueobject.OrderItem;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderDomainMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customerId", source = "customerId")
    @Mapping(target = "restaurantId", source = "restaurantId")
    @Mapping(target = "trackingId", ignore = true)
    @Mapping(target = "price", source = "price")
    @Mapping(target = "items", source = "items")
    Order toOrder(CreateOrderRequestDTO requestDTO);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "customerId", source = "customerId")
    @Mapping(target = "restaurantId", source = "restaurantId")
    @Mapping(target = "trackingId", source = "trackingId")
    @Mapping(target = "price", source = "price")
    OrderResponseDTO toOrderResponseDTO(Order order);

    @Mapping(target = "productId", source = "productId")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "subTotal", source = "subTotal")
    OrderItem toOrderItem(OrderItemRequestDTO itemDTO);

    @IterableMapping(elementTargetType = OrderItem.class)
    List<OrderItem> toOrderItems(List<OrderItemRequestDTO> items);
}