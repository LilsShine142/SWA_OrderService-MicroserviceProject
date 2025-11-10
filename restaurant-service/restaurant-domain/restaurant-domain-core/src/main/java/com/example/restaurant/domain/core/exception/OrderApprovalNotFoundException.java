package com.example.restaurant.domain.core.exception;

import com.example.restaurant.domain.core.exception.RestaurantDomainException;

public class OrderApprovalNotFoundException extends RestaurantDomainException {
    public OrderApprovalNotFoundException(String message) {
        super(message);
    }
}