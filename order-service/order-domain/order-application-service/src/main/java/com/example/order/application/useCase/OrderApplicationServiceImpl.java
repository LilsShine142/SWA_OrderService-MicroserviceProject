//package com.example.order.application.useCase;
//
//import com.example.order.application.dto.*;
//import com.example.order.application.ports.input.service.OrderApplicationService;
//import com.example.order.application.ports.output.OrderRepository;
//import com.example.order.application.ports.output.publisher.*;
//import com.example.order.application.mapper.OrderDataMapper;
//import com.example.order.domain.core.entity.Order;
//import com.example.order.domain.core.event.OrderCreatedEvent;
//import com.example.order.domain.core.event.OrderCancelledEvent;
//import com.example.order.domain.core.exception.OrderDomainException;
//import com.example.order.domain.core.exception.OrderNotFoundException;
//import com.example.order.domain.core.service.OrderDomainService;
//import com.example.order.domain.core.valueobject.TrackingId;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.UUID;
//
//@Service
//public class OrderApplicationServiceImpl implements OrderApplicationService {
//
//    private static final Logger LOG = LoggerFactory.getLogger(OrderApplicationServiceImpl.class);
//
//    private final OrderRepository orderRepository;
//    private final OrderCreatedPaymentRequestPublisher orderCreatedPublisher;
//    private final OrderCancelledEventPublisher orderCancelledPublisher;
//    private final OrderDataMapper orderDataMapper;
//    private final OrderDomainService orderDomainService;
//
//    public OrderApplicationServiceImpl(OrderRepository orderRepository,
//                                       @Qualifier("logOnlyOrderCreatedPublisher") OrderCreatedPaymentRequestPublisher orderCreatedPublisher,
//                                       @Qualifier("logOnlyOrderCancelledEventPublisher") OrderCancelledEventPublisher orderCancelledPublisher,
//                                       OrderDataMapper orderDataMapper,
//                                       OrderDomainService orderDomainService) {
//        this.orderRepository = orderRepository;
//        this.orderCreatedPublisher = orderCreatedPublisher;
//        this.orderCancelledPublisher = orderCancelledPublisher;
//        this.orderDataMapper = orderDataMapper;
//        this.orderDomainService = orderDomainService;
//    }
//
//    @Override
//    @Transactional
//    public CreateOrderResponse createOrder(CreateOrderCommand command) {
//        LOG.info("Bắt đầu xử lý tạo đơn hàng cho khách hàng: {}", command.getCustomerId());
//
//        // 1. DTO -> Entity
//        Order order = orderDataMapper.createOrderCommandToOrder(command);
//
//        // 2. Logic Domain (Nếu lỗi "Tổng tiền không khớp", nó sẽ throw OrderDomainException tại đây)
//        OrderCreatedEvent event = orderDomainService.validateAndInitializeOrder(order);
//
//        // 3. Save DB
//        Order savedOrder = orderRepository.save(order);
//        LOG.info("Đã lưu Order, Tracking ID: {}", savedOrder.getTrackingId().value());
//
//        // 4. Bắn Event
//        orderCreatedPublisher.publish(event);
//
//        // 5. Return
//        return orderDataMapper.orderToCreateOrderResponse(savedOrder, "Order created successfully");
//    }
//
//    @Override
//    @Transactional(readOnly = true)
//    public TrackOrderResponse trackOrder(TrackOrderQuery query) {
//        TrackingId trackingId = new TrackingId(query.orderTrackingId());
//
//        Order order = orderRepository.findByTrackingId(trackingId)
//                .orElseThrow(() -> {
//                    LOG.warn("Không tìm thấy đơn hàng: {}", query.orderTrackingId());
//                    return new OrderNotFoundException("Không tìm thấy đơn hàng với ID: " + query.orderTrackingId());
//                });
//
//        return orderDataMapper.orderToTrackOrderResponse(order);
//    }
//
//    @Override
//    @Transactional
//    public CancelOrderResponse cancelOrder(CancelOrderCommand command) {
//        LOG.info("Bắt đầu hủy đơn: {}", command.orderTrackingId());
//
//        TrackingId trackingId = new TrackingId(command.orderTrackingId());
//        Order order = orderRepository.findByTrackingId(trackingId)
//                .orElseThrow(() -> new OrderNotFoundException("Không tìm thấy đơn hàng để hủy."));
//
//        // Logic hủy (có thể throw Exception nếu đơn đã hoàn thành)
//        order.cancel(List.of(command.reason()));
//
//        Order cancelledOrder = orderRepository.save(order);
//
//        // Publish Event
//        orderCancelledPublisher.publish(new OrderCancelledEvent(cancelledOrder));
//
//        return orderDataMapper.orderToCancelOrderResponse(cancelledOrder, "Order cancelled successfully");
//    }
//
//    // Các method system (processRestaurantApproval) tương tự, bỏ try-catch wrap đi
//    @Override
//    @Transactional
//    public void processRestaurantApproval(UUID orderId) {
//        LOG.info("Processing restaurant approval for order: {}", orderId);
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));
//        order.approve();
//        orderRepository.save(order);
//    }
//}





package com.example.order.application.useCase;

import com.example.order.application.dto.*;
import com.example.order.application.ports.input.service.OrderApplicationService;
import com.example.order.application.ports.output.OrderRepository;
import com.example.order.application.ports.output.publisher.OrderCancelledEventPublisher;
import com.example.order.application.ports.output.publisher.OrderCreatedPaymentRequestPublisher;
import com.example.order.application.mapper.OrderDataMapper;
import com.example.order.application.ports.output.publisher.OrderPaidPublisher;
import com.example.order.domain.core.entity.Order;
import com.example.order.domain.core.event.OrderCancelledEvent;
import com.example.order.domain.core.event.OrderCreatedEvent;
import com.example.order.domain.core.exception.OrderNotFoundException;
import com.example.order.domain.core.service.OrderDomainService;
import com.example.order.domain.core.valueobject.TrackingId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderApplicationServiceImpl implements OrderApplicationService {

    private final OrderRepository orderRepository;
    private final OrderCreatedPaymentRequestPublisher orderCreatedPublisher;
    private final OrderCancelledEventPublisher orderCancelledPublisher;
    private final OrderPaidPublisher orderPaidPublisher;
    private final OrderDataMapper orderDataMapper;
    private final OrderDomainService orderDomainService;

    // Constructor Injection
    public OrderApplicationServiceImpl(OrderRepository orderRepository,
                                       // ❌ ĐÃ XÓA @Qualifier("logOnly...") để dùng Kafka thật
                                       OrderCreatedPaymentRequestPublisher orderCreatedPublisher,
                                       // Nếu bạn chưa làm Kafka cho Cancel thì giữ Qualifier này,
                                       // nếu làm rồi thì xóa luôn Qualifier đi.
                                       OrderCancelledEventPublisher orderCancelledPublisher, OrderPaidPublisher orderPaidPublisher,
                                       OrderDataMapper orderDataMapper,
                                       OrderDomainService orderDomainService) {
        this.orderRepository = orderRepository;
        this.orderCreatedPublisher = orderCreatedPublisher;
        this.orderCancelledPublisher = orderCancelledPublisher;
        this.orderPaidPublisher = orderPaidPublisher;
        this.orderDataMapper = orderDataMapper;
        this.orderDomainService = orderDomainService;
    }

    @Override
    @Transactional
    public CreateOrderResponse createOrder(CreateOrderCommand command) {
        // SỬA: Dùng .getCustomerId()
        log.info("Bắt đầu xử lý tạo đơn hàng cho khách hàng: {}", command.getCustomerId());

        // 1. DTO -> Entity
        Order order = orderDataMapper.createOrderCommandToOrder(command);

        // 2. Logic Domain
        // Nếu validate sai (ví dụ: Tổng tiền không khớp), Domain sẽ throw OrderDomainException.
        // GlobalExceptionHandler sẽ bắt lỗi này và trả về JSON đẹp cho Client.
        OrderCreatedEvent event = orderDomainService.validateAndInitializeOrder(order);

        // 3. Save DB
        Order savedOrder = orderRepository.save(order);
        log.info("Đã lưu Order thành công, Tracking ID: {}", savedOrder.getTrackingId().value());

        // 4. Bắn Event (Sẽ dùng KafkaOrderCreatedPublisher vì đã có @Primary)
        orderCreatedPublisher.publish(event);

        // 5. Return Response
        return orderDataMapper.orderToCreateOrderResponse(savedOrder, "Order created successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public TrackOrderResponse trackOrder(TrackOrderQuery query) {
        // SỬA: Dùng .getOrderTrackingId()
        TrackingId trackingId = new TrackingId(query.getOrderTrackingId());

        Order order = orderRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> {
                    log.warn("Không tìm thấy đơn hàng: {}", query.getOrderTrackingId());
                    return new OrderNotFoundException("Không tìm thấy đơn hàng với ID: " + query.getOrderTrackingId());
                });

        return orderDataMapper.orderToTrackOrderResponse(order);
    }

    @Override
    @Transactional
    public CancelOrderResponse cancelOrder(CancelOrderCommand command) {
        // SỬA: Dùng .getOrderTrackingId()
        log.info("Bắt đầu hủy đơn: {}", command.getOrderTrackingId());

        TrackingId trackingId = new TrackingId(command.getOrderTrackingId());
        Order order = orderRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new OrderNotFoundException("Không tìm thấy đơn hàng để hủy."));

        // Logic hủy (có thể throw Exception nếu đơn đã hoàn thành)
        // SỬA: Dùng .getReason()
        order.cancel(List.of(command.getReason()));

        Order cancelledOrder = orderRepository.save(order);

        // Publish Event
        orderCancelledPublisher.publish(new OrderCancelledEvent(cancelledOrder));

        return orderDataMapper.orderToCancelOrderResponse(cancelledOrder, "Order cancelled successfully");
    }

    @Override
    @Transactional
    public void processRestaurantApproval(UUID orderId) {
        log.info("Processing restaurant approval for order: {}", orderId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));
        order.approve();
        orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderSummaryResponse> findAllOrders() {
        // 1. Lấy tất cả từ DB
        List<Order> orders = orderRepository.findAll();

        // 2. Map sang DTO Summary
        return orders.stream()
                .map(order -> orderDataMapper.orderToOrderSummaryResponse(order))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void payOrder(UUID orderId) {
        log.info("💰 Xử lý thanh toán thành công cho đơn: {}", orderId);

        // 1. Tìm đơn hàng
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));

        // 2. Chuyển trạng thái sang PAID (Logic trong Domain Entity)
        order.pay();
        orderRepository.save(order);

        // 3. Bắn event sang Restaurant (để họ biết có đơn mới đã trả tiền)
        OrderPaidEvent event = OrderPaidEvent.builder()
                .orderId(order.getId().value())
                .restaurantId(order.getRestaurantId().value())
                .items(order.getItems().stream()
                        .map(item -> OrderPaidEvent.OrderItemDto.builder()
                                .productId(item.getProductId().value())
                                .quantity(item.getQuantity())
                                .build())
                        .collect(Collectors.toList()))
                .build();

        orderPaidPublisher.publish(event);
    }

    @Override
    @Transactional
    public void approveOrder(UUID orderId) {
        log.info("👨‍🍳 Nhà hàng đã duyệt đơn: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));

        // 2. Chuyển trạng thái sang APPROVED
        order.approve();
        orderRepository.save(order);

        // Có thể bắn thêm event OrderConfirmed gửi về cho Customer biết (nếu cần)
    }
}