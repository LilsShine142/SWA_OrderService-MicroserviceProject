package com.example.orderService.orderApplication.command;

import com.example.orderService.orderApplication.ports.OrderApplicationService;
import com.example.orderService.orderDomain.entity.Order;
import com.example.orderService.orderDomain.service.OrderDomainService;
import com.example.orderService.orderDomain.valueobject.OrderItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateOrderCommandHandler {
    private final OrderApplicationService orderApplicationService;
    private final OrderDomainService domainService;

    public Order handle(UpdateOrderCommand command) {
        log.info("Cập nhật đơn hàng với ID: {}", command.getId());

        Order order = orderApplicationService.findById(command.getId())
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tìm thấy"));

        order.setStatus(command.getStatus());
        order.setFailureMessages(command.getFailureMessages());
        order.setItems(command.getItems().stream()
                .map(item -> new OrderItem(
                        item.getProductId(),
                        item.getPrice(),
                        item.getQuantity(),
                        item.getSubTotal()
                ))
                .collect(Collectors.toList()));

        domainService.validateOrder(order);
        return orderApplicationService.updateOrder(order);
    }
}