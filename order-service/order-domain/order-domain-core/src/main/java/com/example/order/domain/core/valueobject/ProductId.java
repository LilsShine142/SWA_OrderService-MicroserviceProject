package com.example.order.domain.core.valueobject;

import java.util.UUID;

/**
 * Value Object: ProductId
 * SỬA: Đổi từ Long sang UUID để khớp với SQL
 */
public record ProductId(UUID value) {}