package com.example.orderService.orderApplication.command;


import com.example.orderService.orderApplication.ports.OrderApplicationService;
import com.example.orderService.orderApplication.ports.OrderEventPublisher;
import com.example.orderService.orderDomain.entity.Order;
import com.example.orderService.orderDomain.event.OrderCreatedEvent;
import com.example.orderService.orderDomain.service.OrderDomainService;
import com.example.orderService.orderDomain.valueobject.OrderItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateOrderCommandHandler {
    private final OrderApplicationService orderApplicationService;
    private final OrderDomainService domainService;
    private final OrderEventPublisher eventPublisher;

    public Order handle(CreateOrderCommand command) {
        log.info("Xử lý tạo đơn hàng cho khách hàng: {}", command.getCustomerId());

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setCustomerId(command.getCustomerId());
        order.setRestaurantId(command.getRestaurantId());
        order.setTrackingId(UUID.randomUUID());
        order.setPrice(command.getPrice());
        order.setStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());

        order.setItems(command.getItems().stream()
                .map(item -> new OrderItem(
                        item.getProductId(),
                        item.getPrice(),
                        item.getQuantity(),
                        item.getSubTotal()
                ))
                .collect(Collectors.toList()));

        domainService.validateOrder(order);

        Order savedOrder = orderApplicationService.createOrder(order);
        eventPublisher.publish(new OrderCreatedEvent(savedOrder.getId()));

        return savedOrder;
    }
}