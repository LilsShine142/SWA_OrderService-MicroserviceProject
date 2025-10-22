package com.example.orderService.orderApplication.query;

import com.example.orderService.orderApplication.dto.response.OrderResponseDTO;
import com.example.orderService.orderApplication.mapper.OrderDomainMapper;
import com.example.orderService.orderApplication.ports.OrderApplicationService;
import com.example.orderService.orderDomain.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetOrderQueryHandler {
    private final OrderApplicationService orderApplicationService;
    private final OrderDomainMapper mapper;

    public OrderResponseDTO handle(GetOrderQuery query) {
        Optional<Order> order = orderApplicationService.findById(query.getId());
        return order.map(mapper::toOrderResponseDTO)
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tìm thấy"));
    }
}