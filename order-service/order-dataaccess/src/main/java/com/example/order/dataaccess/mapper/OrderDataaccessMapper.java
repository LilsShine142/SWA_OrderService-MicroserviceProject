package com.example.order.dataaccess.mapper;




import com.example.order.dataaccess.entity.OrderEntity;
import com.example.order.domain.core.entity.Order;
import com.example.order.domain.core.entity.OrderItem;
import com.example.order.domain.core.valueobject.*; // Import các Value Objects
import com.example.order.dataaccess.entity.OrderItemEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper: Chuyển đổi giữa Domain Entity và JPA Entity
 * Vị trí: order-dataaccess/mapper/
 */
@Component
public class OrderDataaccessMapper {

    /**
     * Chuyển từ Domain (Order) -> JPA (OrderEntity) [cite: 370-373]
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

        // Map danh sách OrderItem con
        entity.setItems(orderItemsToOrderItemEntities(order.getItems(), entity));

        // (Bạn cần code thêm logic map StreetAddress -> các cột address trong OrderEntity)

        return entity;
    }

    /**
     * Chuyển từ JPA (OrderEntity) -> Domain (Order) [cite: 376-378]
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
                // (Bạn cần code thêm logic map các cột address -> StreetAddress)
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
                    OrderItem item = OrderItem.builder()
                            .productId(new ProductId(entity.getProductId()))
                            .quantity(entity.getQuantity())
                            .price(new Money(entity.getPrice()))
                            .build();

                    // Gán ID (từ CSDL) cho Domain Entity
                    item.setId(new OrderItemId(entity.getId()));
                    return item;
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
                    // (đã được gán lúc initializeOrder())
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