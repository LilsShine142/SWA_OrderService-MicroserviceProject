package com.example.orderService.orderInfrastructure.persistence.mapper;

import com.example.orderService.orderDomain.entity.Order;
import com.example.orderService.orderDomain.valueobject.OrderItem;
import com.example.orderService.orderInfrastructure.persistence.entity.OrderEntity;
import com.example.orderService.orderInfrastructure.persistence.entity.OrderItemEntity;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderPersistenceMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "customerId", source = "customerId")
    @Mapping(target = "restaurantId", source = "restaurantId")
    @Mapping(target = "trackingId", source = "trackingId")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "items", source = "items")
    Order toDomain(OrderEntity entity);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "customerId", source = "customerId")
    @Mapping(target = "restaurantId", source = "restaurantId")
    @Mapping(target = "trackingId", source = "trackingId")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "status", source = "status.name")
    @Mapping(target = "failureMessages", source = "failureMessages")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "items", source = "items")
    OrderEntity toEntity(Order order);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "orderId", source = "orderId")
    @Mapping(target = "productId", source = "productId")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "subTotal", source = "subTotal")
    OrderItemEntity toEntityItem(OrderItem orderItem);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "orderId", source = "orderId")
    @Mapping(target = "productId", source = "productId")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "subTotal", source = "subTotal")
    OrderItem toDomainItem(OrderItemEntity entity);

    @IterableMapping(elementTargetType = OrderItem.class)
    List<OrderItem> toDomainItems(List<OrderItemEntity> entities);

    @IterableMapping(elementTargetType = OrderItemEntity.class)
    List<OrderItemEntity> toEntityItems(List<OrderItem> items);
}
