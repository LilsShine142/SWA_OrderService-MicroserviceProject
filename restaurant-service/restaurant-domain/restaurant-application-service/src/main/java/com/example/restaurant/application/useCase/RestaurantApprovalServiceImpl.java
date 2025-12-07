//package com.example.restaurant.application.useCase;
//
//import com.example.restaurant.application.dto.request.ApproveOrderCommand;
//import com.example.restaurant.application.dto.request.RejectOrderCommand;
//import com.example.restaurant.application.dto.response.OrderApprovalResponse;
//import com.example.restaurant.application.mapper.RestaurantDataMapper;
//import com.example.restaurant.application.ports.input.service.RestaurantApplicationService;
//import com.example.restaurant.application.ports.output.publisher.MessageRestaurantPublisherPort;
//import com.example.restaurant.application.ports.output.repository.RestaurantRepositoryPort;
//import com.example.restaurant.domain.core.entity.OrderApproval;
//import com.example.restaurant.domain.core.entity.OrderItem;
//import com.example.restaurant.domain.core.entity.Restaurant;
//import com.example.restaurant.domain.core.event.OrderApprovedEvent;
//import com.example.restaurant.domain.core.event.OrderRejectedEvent;
//import com.example.restaurant.domain.core.event.PaymentCompletedEvent;
//import com.example.restaurant.domain.core.event.PaymentFailedEvent;
//import com.example.restaurant.domain.core.service.RestaurantDomainService;
//import com.example.restaurant.domain.core.service.RestaurantDomainServiceImpl;
//import com.example.restaurant.domain.core.valueobject.ApprovalId;
//import com.example.restaurant.domain.core.valueobject.ApprovalStatus;
//import com.example.restaurant.domain.core.valueobject.RestaurantId;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.time.ZonedDateTime;
//import java.util.List;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class RestaurantApprovalServiceImpl implements RestaurantApplicationService {
//
//    private final RestaurantRepositoryPort repositoryPort;
//    private final MessageRestaurantPublisherPort publisherPort;
//    private final RestaurantDomainService domainService = new RestaurantDomainServiceImpl();
//    private final RestaurantDataMapper restaurantDataMapper;
//
//    @Override
//    @Transactional
//    public void processOrderApproval(PaymentCompletedEvent event) {
//        System.out.println("2. Processing order approval for orderId: " + event.getOrderId());
//
//        Restaurant restaurant = repositoryPort.findById(new RestaurantId(event.getRestaurantId()))
//                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
//
//        // Tạo approval với PENDING trước
//        OrderApproval approval = OrderApproval.builder()
//                .orderId(event.getOrderId())
//                .restaurantId(restaurant.getId())
//                .approvalStatus(ApprovalStatus.PENDING)
//                .createdAt(ZonedDateTime.now())
//                .build();
//
//        try {
//            // Gọi approve → bên trong sẽ thay đổi approvalStatus thành APPROVED
//            OrderApprovedEvent approvedEvent = domainService.approveOrder(restaurant, approval, event.getItems());
//            publisherPort.publish(approvedEvent);
//
//            // In ra để debug (tùy chọn)
//            log.info("Order {} APPROVED", event.getOrderId());
//
//        } catch (Exception e) {
//            // Gọi reject → bên trong sẽ thay đổi approvalStatus thành REJECTED
//            OrderRejectedEvent rejectedEvent = domainService.rejectOrder(
//                    restaurant, approval, event.getItems(), e.getMessage());
//            publisherPort.publish(rejectedEvent);
//
//            log.warn("Order {} REJECTED: {}", event.getOrderId(), e.getMessage());
//        }
//
//        // Lưu SAU KHI domainService đã thay đổi status → đúng trạng thái thật
//        repositoryPort.saveApproval(approval);
//    }
//
//    @Override
//    @Transactional
//    public void processPaymentFailed(PaymentFailedEvent event) {
//        Restaurant restaurant = repositoryPort.findById(new RestaurantId(event.getRestaurantId()))
//                .orElseThrow(() -> new RuntimeException("Restaurant not found"));
//
//        OrderApproval approval = OrderApproval.builder()
//                .orderId(event.getOrderId())
//                .restaurantId(restaurant.getId())
//                .approvalStatus(ApprovalStatus.PENDING)
//                .createdAt(ZonedDateTime.now())
//                .build();
//
//        // Thanh toán thất bại → luôn reject
//        OrderRejectedEvent rejectedEvent = domainService.rejectOrder(
//                restaurant, approval, event.getItems(), event.getFailureReason());
//
//        publisherPort.publish(rejectedEvent);
//
//        // Lưu với trạng thái REJECTED (đã được thay đổi trong domainService)
//        repositoryPort.saveApproval(approval);
//    }
//
//    // APPROVE
//    @Override
//    public OrderApprovalResponse approveOrder(ApproveOrderCommand command) {
//        try {
//            Restaurant restaurant = repositoryPort.findById(new RestaurantId(command.getRestaurantId()))
//                    .orElseThrow(() -> new IllegalArgumentException("Restaurant không tồn tại"));
//
//            OrderApproval approval = restaurantDataMapper.approveOrderCommandToOrderApproval(command);
//            approval.setId(new ApprovalId(UUID.randomUUID()));
//
//            List<OrderItem> orderItems = command.getItems().stream()
//                    .map(item -> OrderItem.builder()
//                            .productId(item.getProductId())
//                            .price(item.getPrice())
//                            .quantity(item.getQuantity())
//                            .subTotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
//                            .build())
//                    .collect(Collectors.toList());
//
//            OrderApprovedEvent approvedEvent = domainService.approveOrder(restaurant, approval, orderItems);
//
//            repositoryPort.saveApproval(approval);
//            publisherPort.publish(approvedEvent);
//
//            return restaurantDataMapper.toSuccessResponse(approval, "Duyệt đơn hàng thành công");
//
//        } catch (IllegalStateException | IllegalArgumentException e) {
//            log.warn("Approve order failed: {}", e.getMessage());
//            return restaurantDataMapper.toFailureResponse(command.getOrderId(), e.getMessage());
//        } catch (Exception e) {
//            log.error("Unexpected error during approve order", e);
//            return restaurantDataMapper.toErrorResponse(command.getOrderId(), "Lỗi hệ thống, vui lòng thử lại sau");
//        }
//    }
//
//    @Override
//    public OrderApprovalResponse rejectOrder(RejectOrderCommand command) {
//        try {
//            Restaurant restaurant = repositoryPort.findById(new RestaurantId(command.getRestaurantId()))
//                    .orElseThrow(() -> new IllegalArgumentException("Restaurant không tồn tại"));
//
//            OrderApproval approval = restaurantDataMapper.rejectOrderCommandToOrderApproval(command);
//            approval.setId(new ApprovalId(UUID.randomUUID()));
//
//            OrderRejectedEvent rejectedEvent = domainService.rejectOrder(
//                    restaurant, approval, List.of(), command.getReason());
//
//            repositoryPort.saveApproval(approval);
//            publisherPort.publish(rejectedEvent);
//
//            return restaurantDataMapper.toSuccessResponse(approval,
//                    "Từ chối đơn hàng thành công: " + command.getReason());
//
//        } catch (IllegalStateException | IllegalArgumentException e) {
//            log.warn("Reject order failed: {}", e.getMessage());
//            return restaurantDataMapper.toFailureResponse(command.getOrderId(), e.getMessage());
//        } catch (Exception e) {
//            log.error("Unexpected error during reject order", e);
//            return restaurantDataMapper.toErrorResponse(command.getOrderId(), "Lỗi hệ thống, vui lòng thử lại sau");
//        }
//    }
//}

















package com.example.restaurant.application.useCase;

import com.example.common_messaging.dto.event.OrderPaidEvent;
import com.example.restaurant.application.dto.request.ApproveOrderCommand;
import com.example.restaurant.application.dto.request.RejectOrderCommand;
import com.example.restaurant.application.dto.response.OrderApprovalResponse;
import com.example.restaurant.application.mapper.RestaurantDataMapper;
import com.example.restaurant.application.ports.input.service.RestaurantApplicationService;
import com.example.restaurant.application.ports.output.publisher.MessageRestaurantPublisherPort;
import com.example.restaurant.application.ports.output.repository.RestaurantRepositoryPort;
import com.example.restaurant.domain.core.entity.OrderApproval;
import com.example.restaurant.domain.core.entity.OrderItem;
import com.example.restaurant.domain.core.entity.Restaurant;
import com.example.restaurant.domain.core.event.OrderApprovedEvent;
import com.example.restaurant.domain.core.event.OrderRejectedEvent;
import com.example.restaurant.domain.core.event.PaymentCompletedEvent;
import com.example.restaurant.domain.core.event.PaymentFailedEvent;
import com.example.restaurant.domain.core.service.RestaurantDomainService;
import com.example.restaurant.domain.core.service.RestaurantDomainServiceImpl;
import com.example.restaurant.domain.core.valueobject.ApprovalId;
import com.example.restaurant.domain.core.valueobject.ApprovalStatus;
import com.example.restaurant.domain.core.valueobject.RestaurantId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantApprovalServiceImpl implements RestaurantApplicationService {

    private final RestaurantRepositoryPort repositoryPort;
    private final MessageRestaurantPublisherPort publisherPort;
    private final RestaurantDomainService domainService = new RestaurantDomainServiceImpl();
    private final RestaurantDataMapper restaurantDataMapper;

    // Giả lập cache trạng thái order (orderId -> status)
    private final ConcurrentHashMap<String, String> orderStatusCache = new ConcurrentHashMap<>();

    @Override
    @Transactional
    public void processOrderApproval(PaymentCompletedEvent event) {
        log.info("Restaurant received PaymentCompletedEvent for orderId: {}", event.getOrderId());

        Restaurant restaurant = repositoryPort.findById(new RestaurantId(event.getRestaurantId()))
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + event.getRestaurantId()));

        OrderApproval approval = OrderApproval.builder()
                .orderId(event.getOrderId())
                .restaurantId(restaurant.getId())
                .approvalStatus(ApprovalStatus.PENDING)
                .createdAt(ZonedDateTime.now())
                .build();

        approval.setId(new ApprovalId(UUID.randomUUID()));

        try {
            // GỌI DOMAIN SERVICE – ĐÚNG LUỒNG SAGA
            OrderApprovedEvent approvedEvent = domainService.approveOrder(restaurant, approval, event.getItems());
            publisherPort.publish(approvedEvent);
            log.info("Order {} APPROVED and OrderApprovedEvent published", event.getOrderId());

        } catch (IllegalStateException e) {
            // Domain service ném lỗi → từ chối đơn
            OrderRejectedEvent rejectedEvent = domainService.rejectOrder(
                    restaurant, approval, event.getItems(), e.getMessage());
            publisherPort.publish(rejectedEvent);
            log.warn("Order {} REJECTED: {}", event.getOrderId(), e.getMessage());

        } catch (Exception e) {
            // Lỗi bất ngờ → từ chối đơn với lý do hệ thống
            OrderRejectedEvent rejectedEvent = domainService.rejectOrder(
                    restaurant, approval, event.getItems(), "Lỗi hệ thống khi xử lý đơn hàng");
            publisherPort.publish(rejectedEvent);
            log.error("Order {} REJECTED due to unexpected error", event.getOrderId(), e);
        }

        // Lưu trạng thái cuối cùng (APPROVED hoặc REJECTED)
        repositoryPort.saveApproval(approval);
    }

    @Override
    @Transactional
    public void processPaymentFailed(PaymentFailedEvent event) {
        log.info("Restaurant received PaymentFailedEvent for orderId: {}", event.getOrderId());

        Restaurant restaurant = repositoryPort.findById(new RestaurantId(event.getRestaurantId()))
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        OrderApproval approval = OrderApproval.builder()
                .orderId(event.getOrderId())
                .restaurantId(restaurant.getId())
                .approvalStatus(ApprovalStatus.PENDING)
                .createdAt(ZonedDateTime.now())
                .build();

        // Thanh toán thất bại → luôn từ chối
        OrderRejectedEvent rejectedEvent = domainService.rejectOrder(
                restaurant, approval, event.getItems(), "Thanh toán thất bại: " + event.getFailureReason());

        publisherPort.publish(rejectedEvent);
        approval.setId(new ApprovalId(UUID.randomUUID()));
        repositoryPort.saveApproval(approval);

        log.info("Order {} REJECTED due to payment failure", event.getOrderId());
    }

    // APPROVE
    @Override
    public OrderApprovalResponse approveOrder(ApproveOrderCommand command) {
        // Kiểm tra trạng thái order từ cache
        String orderStatus = orderStatusCache.get(command.getOrderId().toString());
        if (!"PAID".equals(orderStatus)) {
            return restaurantDataMapper.toFailureResponse(command.getOrderId(),
                    "Đơn hàng chưa được thanh toán. Trạng thái hiện tại: " + orderStatus);
        }

        try {
            Restaurant restaurant = repositoryPort.findById(new RestaurantId(command.getRestaurantId()))
                    .orElseThrow(() -> new IllegalArgumentException("Restaurant không tồn tại"));

            OrderApproval approval = restaurantDataMapper.approveOrderCommandToOrderApproval(command);
            approval.setId(new ApprovalId(UUID.randomUUID()));
            approval = approval.toBuilder()
                    .approvalStatus(ApprovalStatus.PENDING)
                    .createdAt(ZonedDateTime.now())
                    .build();

            List<OrderItem> orderItems = command.getItems().stream()
                    .map(item -> OrderItem.builder()
                            .productId(item.getProductId())
                            .price(item.getPrice())
                            .quantity(item.getQuantity())
                            .subTotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                            .build())
                    .collect(Collectors.toList());

            OrderApprovedEvent approvedEvent = domainService.approveOrder(restaurant, approval, orderItems);

            repositoryPort.saveApproval(approval);
            publisherPort.publish(approvedEvent);

            return restaurantDataMapper.toSuccessResponse(approval, "Duyệt đơn hàng thành công");

        } catch (IllegalStateException e) {
            return restaurantDataMapper.toFailureResponse(command.getOrderId(), e.getMessage());
        } catch (Exception e) {
            log.error("Error approving order", e);
            return restaurantDataMapper.toErrorResponse(command.getOrderId(), "Lỗi hệ thống");
        }
    }

    @Override
    public OrderApprovalResponse rejectOrder(RejectOrderCommand command) {
        try {
            Restaurant restaurant = repositoryPort.findById(new RestaurantId(command.getRestaurantId()))
                    .orElseThrow(() -> new IllegalArgumentException("Restaurant không tồn tại"));

            OrderApproval approval = restaurantDataMapper.rejectOrderCommandToOrderApproval(command);
            approval.setId(new ApprovalId(UUID.randomUUID()));
            approval = approval.toBuilder()
                    .approvalStatus(ApprovalStatus.PENDING)
                    .createdAt(ZonedDateTime.now())
                    .build();

            OrderRejectedEvent rejectedEvent = domainService.rejectOrder(
                    restaurant, approval, List.of(), command.getReason());

            repositoryPort.saveApproval(approval);
            publisherPort.publish(rejectedEvent);

            return restaurantDataMapper.toSuccessResponse(approval,
                    "Từ chối đơn hàng thành công: " + command.getReason());

        } catch (IllegalStateException | IllegalArgumentException e) {
            log.warn("Reject order failed: {}", e.getMessage());
            return restaurantDataMapper.toFailureResponse(command.getOrderId(), e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during reject order", e);
            return restaurantDataMapper.toErrorResponse(command.getOrderId(), "Lỗi hệ thống, vui lòng thử lại sau");
        }
    }

    @Override
    @Transactional
    public void completeOrderApproval(OrderPaidEvent event) {
        log.info("Xử lý logic nghiệp vụ cho Order đã thanh toán: {}", event.getOrderId());

        // Cập nhật cache trạng thái order
        orderStatusCache.put(event.getOrderId().toString(), event.getStatus());

        // Tạo Entity (Domain Object)
        OrderApproval orderApproval = OrderApproval.builder()
                .orderId(event.getOrderId())
                .restaurantId(new RestaurantId(event.getRestaurantId()))
                .approvalStatus(ApprovalStatus.PENDING) // Logic nghiệp vụ set default
                .build();

        orderApproval.setId(new ApprovalId(UUID.randomUUID()));

        // Gọi qua Output Port để lưu (Không gọi trực tiếp JPA)
        repositoryPort.save(orderApproval);
    }

    @Override
    public List<Restaurant> getAllRestaurants() {
        return repositoryPort.findAllRestaurants();
    }

    @Override
    public List<OrderApproval> getAllOrderApprovals() {
        return repositoryPort.findAllOrderApprovals();
    }
}
