package com.example.order.application.useCase;

import com.example.order.application.dto.*;
import com.example.order.application.ports.input.service.OrderApplicationService;
import com.example.order.application.ports.output.OrderRepository;
import com.example.order.application.ports.output.publisher.*;
import com.example.order.application.mapper.OrderDataMapper;
import com.example.order.domain.core.entity.Order;
import com.example.order.domain.core.event.OrderCreatedEvent;
import com.example.order.domain.core.event.OrderCancelledEvent;
import com.example.order.domain.core.exception.OrderDomainException;
import com.example.order.domain.core.exception.OrderNotFoundException;
import com.example.order.domain.core.service.OrderDomainService;
import com.example.order.domain.core.valueobject.TrackingId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class OrderApplicationServiceImpl implements OrderApplicationService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderApplicationServiceImpl.class);

    private final OrderRepository orderRepository;
    private final OrderCreatedPaymentRequestPublisher orderCreatedPublisher;
    private final OrderCancelledEventPublisher orderCancelledPublisher;
    private final OrderDataMapper orderDataMapper;
    private final OrderDomainService orderDomainService;

    public OrderApplicationServiceImpl(OrderRepository orderRepository,
                                       @Qualifier("logOnlyOrderCreatedPublisher") OrderCreatedPaymentRequestPublisher orderCreatedPublisher,
                                       @Qualifier("logOnlyOrderCancelledEventPublisher") OrderCancelledEventPublisher orderCancelledPublisher,
                                       OrderDataMapper orderDataMapper,
                                       OrderDomainService orderDomainService) {
        this.orderRepository = orderRepository;
        this.orderCreatedPublisher = orderCreatedPublisher;
        this.orderCancelledPublisher = orderCancelledPublisher;
        this.orderDataMapper = orderDataMapper;
        this.orderDomainService = orderDomainService;
    }

    @Override
    @Transactional
    public CreateOrderResponse createOrder(CreateOrderCommand command) {
        LOG.info("Bắt đầu xử lý tạo đơn hàng cho khách hàng: {}", command.customerId());

        // 1. DTO -> Entity
        Order order = orderDataMapper.createOrderCommandToOrder(command);

        // 2. Logic Domain (Nếu lỗi "Tổng tiền không khớp", nó sẽ throw OrderDomainException tại đây)
        OrderCreatedEvent event = orderDomainService.validateAndInitializeOrder(order);

        // 3. Save DB
        Order savedOrder = orderRepository.save(order);
        LOG.info("Đã lưu Order, Tracking ID: {}", savedOrder.getTrackingId().value());

        // 4. Bắn Event
        orderCreatedPublisher.publish(event);

        // 5. Return
        return orderDataMapper.orderToCreateOrderResponse(savedOrder, "Order created successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public TrackOrderResponse trackOrder(TrackOrderQuery query) {
        TrackingId trackingId = new TrackingId(query.orderTrackingId());

        Order order = orderRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> {
                    LOG.warn("Không tìm thấy đơn hàng: {}", query.orderTrackingId());
                    return new OrderNotFoundException("Không tìm thấy đơn hàng với ID: " + query.orderTrackingId());
                });

        return orderDataMapper.orderToTrackOrderResponse(order);
    }

    @Override
    @Transactional
    public CancelOrderResponse cancelOrder(CancelOrderCommand command) {
        LOG.info("Bắt đầu hủy đơn: {}", command.orderTrackingId());

        TrackingId trackingId = new TrackingId(command.orderTrackingId());
        Order order = orderRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new OrderNotFoundException("Không tìm thấy đơn hàng để hủy."));

        // Logic hủy (có thể throw Exception nếu đơn đã hoàn thành)
        order.cancel(List.of(command.reason()));

        Order cancelledOrder = orderRepository.save(order);

        // Publish Event
        orderCancelledPublisher.publish(new OrderCancelledEvent(cancelledOrder));

        return orderDataMapper.orderToCancelOrderResponse(cancelledOrder, "Order cancelled successfully");
    }

    // Các method system (processRestaurantApproval) tương tự, bỏ try-catch wrap đi
    @Override
    @Transactional
    public void processRestaurantApproval(UUID orderId) {
        LOG.info("Processing restaurant approval for order: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));
        order.approve();
        orderRepository.save(order);
    }
}