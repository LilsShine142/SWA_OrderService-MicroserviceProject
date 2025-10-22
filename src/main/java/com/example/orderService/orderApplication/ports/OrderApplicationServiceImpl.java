package com.example.orderService.orderApplication.ports;


import com.example.orderService.orderDomain.entity.Order;
import com.example.orderService.orderDomain.event.OrderCancelledEvent;
import com.example.orderService.orderDomain.event.OrderCreatedEvent;
import com.example.orderService.orderDomain.repository.OrderRepository;
import com.example.orderService.orderDomain.service.OrderDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderApplicationServiceImpl implements OrderApplicationService {

    private final OrderRepository orderRepository;
    private final OrderDomainService orderDomainService;
    private final OrderEventPublisher orderEventPublisher;

    @Override
    public Order createOrder(Order order) {
        log.info("Tạo đơn hàng mới với ID: {}", order.getId());
        orderDomainService.validateOrder(order);
        Order savedOrder = orderRepository.save(order);
        orderEventPublisher.publish(new OrderCreatedEvent(savedOrder.getId()));
        return savedOrder;
    }

    @Override
    public Order updateOrder(Order order) {
        log.info("Cập nhật đơn hàng với ID: {}", order.getId());
        orderDomainService.validateOrder(order);
        return orderRepository.save(order);
    }

    @Override
    public void deleteOrder(UUID id) {
        log.info("Xóa đơn hàng với ID: {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tìm thấy"));
        orderRepository.save(order); // Có thể thêm logic hủy trước khi xóa
        orderEventPublisher.publish(new OrderCancelledEvent(id));
    }

    @Override
    public Optional<Order> findById(UUID id) {
        log.info("Tìm đơn hàng với ID: {}", id);
        return orderRepository.findById(id);
    }

    @Override
    public List<Order> findAllOrders() {
        log.info("Lấy danh sách tất cả đơn hàng");
        return orderRepository.findAll();
    }
}