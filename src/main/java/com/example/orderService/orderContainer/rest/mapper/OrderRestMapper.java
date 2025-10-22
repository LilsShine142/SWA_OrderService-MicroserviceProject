package com.example.orderService.orderContainer.rest.mapper;


import com.example.orderService.orderApplication.command.CreateOrderCommand;
import com.example.orderService.orderApplication.dto.request.OrderItemRequestDTO;
import com.example.orderService.orderContainer.rest.dto.CreateOrderRestRequest;
import com.example.orderService.orderContainer.rest.dto.OrderItemRestRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface OrderRestMapper {
    @Mapping(target = "customerId", source = "customerId")
    @Mapping(target = "restaurantId", source = "restaurantId")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "items", source = "items")
    CreateOrderCommand toCreateOrderCommand(CreateOrderRestRequest request);

    @Mapping(target = "productId", source = "productId")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "subTotal", source = "subTotal")
    OrderItemRequestDTO toOrderItemRequestDTO(OrderItemRestRequest itemRequest);

    default CreateOrderCommand toCreateOrderCommandWithItems(CreateOrderRestRequest request) {
        CreateOrderCommand command = toCreateOrderCommand(request);
        if (request.getItems() != null) {
            command.setItems(request.getItems().stream()
                    .map(this::toOrderItemRequestDTO)
                    .collect(Collectors.toList()));
        }
        return command;
    }
}