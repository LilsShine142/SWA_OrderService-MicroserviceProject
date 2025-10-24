package com.example.orderDomain.valueobject;



/**
 * Lớp cơ sở cho tất cả Aggregate Root.
 * Chỉ là một lớp "đánh dấu" (marker) để phân biệt
 * Aggregate Root với các Entity thông thường.
 */
public abstract class AggregateRoot<T> extends BaseEntity<T> {
    // Trong các thiết kế phức tạp hơn, lớp này có thể
    // chứa logic để quản lý Domain Events.
}