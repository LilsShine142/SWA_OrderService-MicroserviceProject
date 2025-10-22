package com.example.orderService.orderDomain.service;

import com.example.orderService.orderDomain.entity.Order;
import com.example.orderService.orderDomain.exception.OrderValidationException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class OrderDomainServiceImpl implements OrderDomainService {
    private static final List<String> VALID_STATUSES = Arrays.asList("PENDING", "PAID", "APPROVED", "CANCELLING", "CANCELLED");

    @Override
    public void validateOrder(Order order) {
        if (!order.isValid()) {
            throw new OrderValidationException("Đơn hàng không hợp lệ");
        }
        if (!VALID_STATUSES.contains(order.getStatus())) {
            throw new OrderValidationException("Trạng thái đơn hàng không hợp lệ: " + order.getStatus());
        }
        if (order.getPrice() == null || order.getPrice() < 0) {
            throw new OrderValidationException("Giá trị đơn hàng phải lớn hơn hoặc bằng 0");
        }
        if (order.getItems().stream().anyMatch(item -> item.getQuantity() == null || item.getQuantity() <= 0)) {
            throw new OrderValidationException("Số lượng món ăn phải lớn hơn 0");
        }
    }
}