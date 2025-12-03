package com.example.restaurant.domain.core.entity;

import com.example.restaurant.domain.core.valueobject.RestaurantId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder(toBuilder = true)
public class Restaurant extends AggregateRoot<RestaurantId> {
    private RestaurantId id;
    private String name;
    private String address;
    private boolean active;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private List<MenuItem> menuItems;

    public void validate() {
        if (name == null || name.isEmpty()) {
            throw new IllegalStateException("Restaurant name cannot be empty");
        }
    }

    public boolean checkAvailability(List<OrderItem> items) {
        // Logic to check if all items are available in menu
        return menuItems.stream().allMatch(menuItem ->
                items.stream().anyMatch(orderItem ->
                        orderItem.getProductId().equals(menuItem.getProductId()) && menuItem.isAvailable()
                )
        );
    }
}
