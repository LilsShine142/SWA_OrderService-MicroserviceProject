package com.example.restaurant.dataaccess.repository;

import com.example.restaurant.dataaccess.entity.MenuItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MenuItemJpaRepository extends JpaRepository<MenuItemEntity, UUID> {

    Optional<MenuItemEntity> findByProductId(UUID productId);

    List<MenuItemEntity> findByRestaurantIdAndAvailableTrue(UUID restaurantId);

    Optional<MenuItemEntity> findByRestaurantIdAndProductId(UUID restaurantId, UUID productId);

    List<MenuItemEntity> findByAvailableTrue();

    @Query("SELECT m FROM MenuItemEntity m WHERE m.restaurantId = :restaurantId AND m.available = true")
    List<MenuItemEntity> findAvailableByRestaurantId(@Param("restaurantId") UUID restaurantId);
}