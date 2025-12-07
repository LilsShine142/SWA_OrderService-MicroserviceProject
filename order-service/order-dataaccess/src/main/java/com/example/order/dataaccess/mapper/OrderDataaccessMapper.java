//package com.example.order.dataaccess.mapper;
//
//
//
//
//import com.example.order.dataaccess.entity.OrderEntity;
//import com.example.order.domain.core.entity.Order;
//import com.example.order.domain.core.entity.OrderItem;
//import com.example.order.domain.core.valueobject.*; // Import các Value Objects
//import com.example.order.dataaccess.entity.OrderItemEntity;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
///**
// * Mapper: Chuyển đổi giữa Domain Entity và JPA Entity
// * Vị trí: order-dataaccess/mapper/
// */
//@Component
//public class OrderDataaccessMapper {
//
//    /**
//     * Chuyển từ Domain (Order) -> JPA (OrderEntity) [cite: 370-373]
//     * Dùng để *LƯU* vào CSDL.
//     */
//    public OrderEntity orderToOrderEntity(Order order) {
//        OrderEntity entity = new OrderEntity();
//        entity.setId(order.getId().value());
//        entity.setCustomerId(order.getCustomerId().value());
//        entity.setRestaurantId(order.getRestaurantId().value());
//        entity.setTrackingId(order.getTrackingId().value());
//        entity.setPrice(order.getPrice().getAmount());
//        entity.setOrderStatus(order.getOrderStatus());
//        entity.setFailureMessages(order.getFailureMessages());
//        entity.setCreatedAt(order.getCreatedAt());
//
//        // Map danh sách OrderItem con
//        entity.setItems(orderItemsToOrderItemEntities(order.getItems(), entity));
//
//        // Map address
//        entity.setStreet(order.getDeliveryAddress().getStreet());
//        entity.setPostalCode(order.getDeliveryAddress().getPostalCode());
//        entity.setCity(order.getDeliveryAddress().getCity());
//
//        return entity;
//    }
//
//    /**
//     * Chuyển từ JPA (OrderEntity) -> Domain (Order) [cite: 376-378]
//     * Dùng khi *ĐỌC* từ CSDL lên.
//     */
//    public Order orderEntityToOrder(OrderEntity entity) {
//        // Tái tạo lại Domain Entity bằng Builder
//        return Order.builder()
//                .orderId(new OrderId(entity.getId()))
//                .customerId(new CustomerId(entity.getCustomerId()))
//                .restaurantId(new RestaurantId(entity.getRestaurantId()))
//                .trackingId(new TrackingId(entity.getTrackingId()))
//                .price(new Money(entity.getPrice()))
//                .orderStatus(entity.getOrderStatus())
//                .failureMessages(entity.getFailureMessages())
//                .createdAt(entity.getCreatedAt())
//                .items(orderItemEntitiesToOrderItems(entity.getItems()))
//                .deliveryAddress(new StreetAddress(UUID.randomUUID(), entity.getStreet(), entity.getPostalCode(), entity.getCity()))
//                .build();
//    }
//
//    // --- Các hàm private hỗ trợ map List ---
//
//    private List<OrderItem> orderItemEntitiesToOrderItems(List<OrderItemEntity> items) {
//        if (items == null) {
//            return new ArrayList<>();
//        }
//        return items.stream()
//                .map(entity -> {
//                    // Dùng Builder của Domain
//                    return OrderItem.builder()
//                            .orderItemId(new OrderItemId(entity.getId().getId())) // Map ID từ DB
//                            .productId(new ProductId(entity.getProductId()))
//                            .quantity(entity.getQuantity())
//                            .price(new Money(entity.getPrice()))
//                            .subTotal(new Money(entity.getSubTotal())) // Map subTotal từ DB
//                            .build();
//                })
//                .collect(Collectors.toList());
//    }
//
//    private List<OrderItemEntity> orderItemsToOrderItemEntities(List<OrderItem> items, OrderEntity orderEntity) {
//        if (items == null) {
//            return new ArrayList<>();
//        }
//        return items.stream()
//                .map(item -> {
//                    OrderItemEntity entity = new OrderItemEntity();
//                    // ID của item được lấy từ Domain Entity
//                    entity.setId(new OrderItemEntityId(item.getId().value(), orderEntity.getId()));
//                    entity.setProductId(item.getProductId().value());
//                    entity.setQuantity(item.getQuantity());
//                    entity.setPrice(item.getPrice().getAmount());
//                    entity.setSubTotal(item.getSubTotal().getAmount());
//
//                    // QUAN TRỌNG: Gán quan hệ 2 chiều
//                    entity.setOrder(orderEntity);
//                    return entity;
//                })
//                .collect(Collectors.toList());
//    }
//}















package com.example.order.dataaccess.mapper;

import com.example.order.dataaccess.entity.OrderEntity;
import com.example.order.dataaccess.entity.OrderItemEntity;
import com.example.order.dataaccess.entity.OrderItemEntityId;
import com.example.order.domain.core.entity.Order;
import com.example.order.domain.core.entity.OrderItem;
import com.example.order.domain.core.valueobject.*; // Import các Value Objects
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Mapper: Chuyển đổi giữa Domain Entity và JPA Entity
 * Vị trí: order-dataaccess/mapper/
 */
@Component
public class OrderDataaccessMapper {

    /**
     * Chuyển từ Domain (Order) -> JPA (OrderEntity)
     * Dùng để *LƯU* vào CSDL.
     */
    public OrderEntity orderToOrderEntity(Order order) {
        OrderEntity entity = new OrderEntity();
        entity.setId(order.getId().value());
        entity.setCustomerId(order.getCustomerId().value());
        entity.setRestaurantId(order.getRestaurantId().value());
        entity.setTrackingId(order.getTrackingId().value());
        entity.setPrice(order.getPrice().getAmount());
        entity.setOrderStatus(order.getOrderStatus());
        entity.setFailureMessages(order.getFailureMessages());
        entity.setCreatedAt(order.getCreatedAt());

        // 1. Map danh sách OrderItem con
        entity.setItems(orderItemsToOrderItemEntities(order.getItems(), entity));

        // 2. Map Address (SỬA LỖI NULL STREET TẠI ĐÂY)
        // Kiểm tra null để tránh lỗi NullPointerException nếu domain thiếu địa chỉ
        StreetAddress address = order.getDeliveryAddress();
        if (address != null) {
            entity.setStreet(address.getStreet());
            entity.setPostalCode(address.getPostalCode());
            entity.setCity(address.getCity());
        }

        return entity;
    }

    /**
     * Chuyển từ JPA (OrderEntity) -> Domain (Order)
     * Dùng khi *ĐỌC* từ CSDL lên.
     */
    public Order orderEntityToOrder(OrderEntity entity) {
        // Tái tạo lại Domain Entity bằng Builder
        return Order.builder()
                .orderId(new OrderId(entity.getId()))
                .customerId(new CustomerId(entity.getCustomerId()))
                .restaurantId(new RestaurantId(entity.getRestaurantId()))
                .trackingId(new TrackingId(entity.getTrackingId()))
                .price(new Money(entity.getPrice()))
                .orderStatus(entity.getOrderStatus())
                .failureMessages(entity.getFailureMessages())
                .createdAt(entity.getCreatedAt())
                .items(orderItemEntitiesToOrderItems(entity.getItems()))
                // 3. Map ngược từ DB lên Domain (Lưu ý: Bỏ UUID nếu StreetAddress không dùng ID)
                .deliveryAddress(new StreetAddress(
                        UUID.randomUUID(),
                        entity.getStreet(),
                        entity.getPostalCode(),
                        entity.getCity()
                ))
                .build();
    }

    // --- Các hàm private hỗ trợ map List ---

    private List<OrderItem> orderItemEntitiesToOrderItems(List<OrderItemEntity> items) {
        if (items == null) {
            return new ArrayList<>();
        }
        return items.stream()
                .map(entity -> {
                    // Dùng Builder của Domain
                    return OrderItem.builder()
                            .id(new OrderItemId(entity.getId())) // Map ID từ DB
                            .productId(new ProductId(entity.getProductId()))
                            .quantity(entity.getQuantity())
                            .price(new Money(entity.getPrice()))
//                            .subTotal(new Money(entity.getSubTotal())) // Map subTotal từ DB
                            .build();
                })
                .collect(Collectors.toList());
    }

    private List<OrderItemEntity> orderItemsToOrderItemEntities(List<OrderItem> items, OrderEntity orderEntity) {
        if (items == null) {
            return new ArrayList<>();
        }
        return items.stream()
                .map(item -> {
                    OrderItemEntity entity = new OrderItemEntity();
                    // ID của item được lấy từ Domain Entity
                    entity.setId(item.getId().value());
                    entity.setProductId(item.getProductId().value());
                    entity.setQuantity(item.getQuantity());
                    entity.setPrice(item.getPrice().getAmount());
                    entity.setSubTotal(item.getSubTotal().getAmount());

                    // QUAN TRỌNG: Gán quan hệ 2 chiều
                    entity.setOrder(orderEntity);
                    return entity;
                })
                .collect(Collectors.toList());
    }
}