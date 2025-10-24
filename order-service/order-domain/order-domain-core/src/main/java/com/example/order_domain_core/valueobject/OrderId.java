package com.example.orderDomain.valueobject;



import java.util.UUID;

// Dùng record cho các ID đơn giản là tiện nhất
public record OrderId(UUID value) {}