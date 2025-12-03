package com.example.order.application.useCase;


import com.example.order.application.dto.*;
import com.example.order.application.ports.input.service.OrderApplicationService;
import com.example.order.application.ports.output.OrderRepository;
import com.example.order.application.ports.output.publisher.OrderCreatedPaymentRequestPublisher;
import com.example.order.domain.core.entity.Order;
import com.example.order.domain.core.event.OrderCreatedEvent;
import com.example.order.domain.core.exception.OrderNotFoundException; // (Bạn nên tự tạo class này)
import com.example.order.domain.core.service.OrderDomainService;
import com.example.order.domain.core.valueobject.TrackingId;

import com.example.order.application.dto.*;
import com.example.order.application.mapper.OrderDataMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * Đây là "Bước 2: Application uses interface"
 * Class này triển khai Input Port (OrderApplicationService)
 * Vị trí: order-application-service/handler/
 */
@Service // Đánh dấu đây là một Spring Bean (Giống @Component)
public class OrderApplicationServiceImpl implements OrderApplicationService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderApplicationServiceImpl.class);

    // --- CÁC CỔNG RA (OUTPUT PORTS) ---
    // (Giống như "private final OrderRepository repository;")

    private final OrderRepository orderRepository;
    private final OrderCreatedPaymentRequestPublisher orderCreatedPublisher;
    // (Bạn sẽ inject thêm các Publisher/Repository khác ở đây)

    // --- CÁC HELPER (MAPPER) ---
    private final OrderDataMapper orderDataMapper;
    private final OrderDomainService orderDomainService;

    // --- CONSTRUCTOR INJECTION ---
    // (Giống như "public OrderService(OrderRepository repository)")
    public OrderApplicationServiceImpl(OrderRepository orderRepository,
                                       @Qualifier("logOnlyOrderCreatedPublisher") OrderCreatedPaymentRequestPublisher orderCreatedPublisher,
                                       OrderDataMapper orderDataMapper,
                                       OrderDomainService orderDomainService) {
        this.orderRepository = orderRepository;
        this.orderCreatedPublisher = orderCreatedPublisher;
        this.orderDataMapper = orderDataMapper;
        this.orderDomainService=orderDomainService;
    }

    /**
     * Triển khai Use Case: Create Order
     */
    @Override
    @Transactional // Đảm bảo tất cả được commit hoặc rollback
    public CreateOrderResponse createOrder(CreateOrderCommand command) {
        LOG.info("Bắt đầu xử lý tạo đơn hàng cho khách hàng: {}", command.customerId());

        // 1. Chuyển DTO -> Domain Entity (dùng Mapper)
        Order order = orderDataMapper.createOrderCommandToOrder(command);

        // 2. GỌI LOGIC NGHIỆP VỤ (Domain)
        OrderCreatedEvent event = orderDomainService.validateAndInitializeOrder(order);
        LOG.info("Đã khởi tạo và validate Order, ID: {}", order.getId().value());

        // 3. GỌI CỔNG RA CSDL (Giống "repository.save(order);")
        Order savedOrder = orderRepository.save(order);
        LOG.info("Đã lưu Order vào CSDL, Tracking ID: {}", savedOrder.getTrackingId().value());

        // 4. GỌI CỔNG RA MESSAGING (Kafka)

        orderCreatedPublisher.publish(event);
        savedOrder.clearDomainEvents();
        LOG.info("Đã publish OrderCreatedEvent lên Kafka.");

        // (Lưu ý: Để an toàn, bạn nên dùng Outbox Pattern [cite: 47])

        // 5. Chuyển đổi kết quả sang DTO Response để trả về
        return orderDataMapper.orderToCreateOrderResponse(savedOrder,
                "Order created successfully");
    }

    /**
     * Triển khai Use Case: Track Order
     */
    @Override
    @Transactional(readOnly = true) // Chỉ đọc, không thay đổi CSDL
    public TrackOrderResponse trackOrder(TrackOrderQuery query) {
        LOG.info("Bắt đầu tìm kiếm đơn hàng: {}", query.orderTrackingId());

        // 1. Dùng Cổng Ra (Repository) để tìm
        TrackingId trackingId = new TrackingId(query.orderTrackingId());
        Order order = orderRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> {
                    LOG.warn("Không tìm thấy đơn hàng với tracking id: {}", query.orderTrackingId());
                    return new OrderNotFoundException("Không tìm thấy đơn hàng.");
                });

        // 2. Map sang DTO Response
        return orderDataMapper.orderToTrackOrderResponse(order);
    }

    /**
     * Triển khai Use Case: Cancel Order
     */
    @Override
    @Transactional
    public CancelOrderResponse cancelOrder(CancelOrderCommand command) {
        LOG.info("Bắt đầu xử lý hủy đơn hàng: {}", command.orderTrackingId());

        // 1. Dùng Cổng Ra (Repository) để tìm
        TrackingId trackingId = new TrackingId(command.orderTrackingId());
        Order order = orderRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> {
                    LOG.warn("Không tìm thấy đơn hàng với tracking id: {}", command.orderTrackingId());
                    return new OrderNotFoundException("Không tìm thấy đơn hàng.");
                });

        // 2. GỌI LOGIC NGHIỆP VỤ (Domain)
        order.cancel(List.of("Đơn hàng đã bị hủy bởi người dùng"));

        // 3. GỌI CỔNG RA CSDL
        Order cancelledOrder = orderRepository.save(order);
        LOG.info("Đã cập nhật trạng thái CANCELLED cho đơn hàng: {}", command.orderTrackingId());

        // 4. (Bạn có thể publish sự kiện OrderCancelledEvent ở đây nếu cần)

        // 5. Map sang DTO Response
        return orderDataMapper.orderToCancelOrderResponse(cancelledOrder,
                "Order cancelled successfully");
    }
}