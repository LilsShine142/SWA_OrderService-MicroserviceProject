package com.example.restaurant.dataaccess.adapter;

import com.example.restaurant.application.ports.output.repository.RestaurantRepositoryPort;
import com.example.restaurant.dataaccess.entity.OrderApprovalEntity;
import com.example.restaurant.dataaccess.entity.RestaurantEntity;
import com.example.restaurant.dataaccess.mapper.OrderApprovalEntityMapper;
import com.example.restaurant.dataaccess.mapper.RestaurantEntityMapper;
import com.example.restaurant.dataaccess.repository.OrderApprovalJpaRepository;
import com.example.restaurant.dataaccess.repository.RestaurantJpaRepository;
import com.example.restaurant.domain.core.entity.OrderApproval;
import com.example.restaurant.domain.core.entity.Restaurant;
import com.example.restaurant.domain.core.valueobject.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RestaurantRepositoryAdapter implements RestaurantRepositoryPort {

    private final RestaurantJpaRepository restaurantJpaRepository;
    private final OrderApprovalJpaRepository approvalJpaRepository;
    private final RestaurantEntityMapper restaurantMapper;
    private final OrderApprovalEntityMapper approvalMapper;

    @Override
    public Optional<Restaurant> findById(RestaurantId id) {
        return restaurantJpaRepository.findById(id.getValue()).map(restaurantMapper::toRestaurant);
    }

    @Override
    public Restaurant save(Restaurant restaurant) {
        RestaurantEntity entity = restaurantMapper.toRestaurantEntity(restaurant);
        return restaurantMapper.toRestaurant(restaurantJpaRepository.save(entity));
    }

    @Override
    public void saveApproval(OrderApproval approval) {
        OrderApprovalEntity entity = approvalMapper.orderApprovalToOrderApprovalEntity(approval);
        approvalJpaRepository.save(entity);
    }

    @Override
    public OrderApproval save(OrderApproval orderApproval) {
        OrderApprovalEntity entity = approvalMapper.orderApprovalToOrderApprovalEntity(orderApproval);
        OrderApprovalEntity savedEntity = approvalJpaRepository.save(entity);
        return approvalMapper.orderApprovalEntityToOrderApproval(savedEntity);
    }

    @Override
    public Optional<OrderApproval> findByOrderId(UUID orderId) {
        Optional<OrderApprovalEntity> entity = approvalJpaRepository.findByOrderId(orderId);
        return entity.map(approvalMapper::orderApprovalEntityToOrderApproval);
    }
}