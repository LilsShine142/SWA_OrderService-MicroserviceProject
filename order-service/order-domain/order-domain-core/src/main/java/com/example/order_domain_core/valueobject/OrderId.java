package com.example.order_domain_core.valueobject;



import java.util.UUID;

// Dùng record cho các ID đơn giản là tiện nhất
public record OrderId(UUID value) {}