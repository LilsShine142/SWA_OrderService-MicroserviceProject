package com.example.restaurant.domain.core.valueobject;

import lombok.Value;
import java.math.BigDecimal;

@Value
public class Money {
    public static final Money ZERO = new Money(BigDecimal.ZERO);
    BigDecimal amount;

    public boolean isGreaterThanZero() {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }
}