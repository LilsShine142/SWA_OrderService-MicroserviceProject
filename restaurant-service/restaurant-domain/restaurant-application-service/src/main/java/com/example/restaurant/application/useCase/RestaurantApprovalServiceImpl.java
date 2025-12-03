package com.example.restaurant.application.useCase;

import com.example.restaurant.application.dto.request.ApproveOrderCommand;
import com.example.restaurant.application.dto.request.RejectOrderCommand;
import com.example.restaurant.application.dto.response.OrderApprovalResponse;
import com.example.restaurant.domain.core.entity.OrderItem;
import com.example.restaurant.domain.core.event.PaymentCompletedEvent;
import com.example.restaurant.domain.core.event.PaymentFailedEvent;
import com.example.restaurant.application.ports.input.service.RestaurantApplicationService;
import com.example.restaurant.application.ports.output.publisher.MessageRestaurantPublisherPort;
import com.example.restaurant.application.ports.output.repository.RestaurantRepositoryPort;
import com.example.restaurant.domain.core.entity.OrderApproval;
import com.example.restaurant.domain.core.entity.Restaurant;
import com.example.restaurant.domain.core.event.OrderApprovedEvent;
import com.example.restaurant.domain.core.event.OrderRejectedEvent;
import com.example.restaurant.domain.core.service.RestaurantDomainService;
import com.example.restaurant.domain.core.service.RestaurantDomainServiceImpl;
import com.example.restaurant.domain.core.valueobject.RestaurantId;
import com.example.restaurant.domain.core.valueobject.ApprovalId;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Validated
@RequiredArgsConstructor
public class RestaurantApprovalServiceImpl implements RestaurantApplicationService {
//    private final RestaurantDomainService domainService;
    private final RestaurantRepositoryPort repositoryPort;
    private final MessageRestaurantPublisherPort publisherPort;

    // TẠO DOMAIN SERVICE THỦ CÔNG – KHÔNG DÙNG @Service TRONG DOMAIN
    private final RestaurantDomainService domainService = new RestaurantDomainServiceImpl();

    @Override
    @Transactional
    public void processOrderApproval(PaymentCompletedEvent event) {
        Restaurant restaurant = repositoryPort.findById(new RestaurantId(event.getRestaurantId())).orElseThrow(() -> new RuntimeException("Restaurant not found"));
        OrderApproval approval = OrderApproval.builder()
                .orderId(event.getOrderId())
                .restaurantId(restaurant.getId())
                .build();

        try {
            OrderApprovedEvent approvedEvent = domainService.approveOrder(restaurant, approval, event.getItems());
            publisherPort.publish(approvedEvent);
        } catch (Exception e) {
            OrderRejectedEvent rejectedEvent = domainService.rejectOrder(restaurant, approval, event.getItems(), e.getMessage());
            publisherPort.publish(rejectedEvent);
        }

        repositoryPort.saveApproval(approval);
    }

    @Override
    @Transactional
    public void processPaymentFailed(PaymentFailedEvent event) {
        Restaurant restaurant = repositoryPort.findById(new RestaurantId(event.getRestaurantId())).orElseThrow(() -> new RuntimeException("Restaurant not found"));
        OrderApproval approval = OrderApproval.builder()
                .orderId(event.getOrderId())
                .restaurantId(restaurant.getId())
                .build();

        OrderRejectedEvent rejectedEvent = domainService.rejectOrder(restaurant, approval, event.getItems(), event.getFailureReason());
        publisherPort.publish(rejectedEvent);

        repositoryPort.saveApproval(approval);
    }

    @Override
    public OrderApprovalResponse approveOrder(ApproveOrderCommand command) {
        Restaurant restaurant = repositoryPort.findById(new RestaurantId(command.getRestaurantId())).orElseThrow(() -> new RuntimeException("Restaurant not found"));
        OrderApproval approval = OrderApproval.builder()
                .orderId(command.getOrderId())
                .restaurantId(restaurant.getId())
                .build();
        approval.setId(new ApprovalId(UUID.randomUUID()));

        List<OrderItem> orderItems = command.getItems().stream()
                .map(item -> OrderItem.builder()
                        .productId(item.getProductId())
                        .name("") // Placeholder, assume no name in command
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .subTotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .build())
                .collect(Collectors.toList());

        // Check availability, throws if not
        domainService.approveOrder(restaurant, approval, orderItems);

        repositoryPort.saveApproval(approval);
        return OrderApprovalResponse.builder()
                .approvalId(approval.getId().getValue())
                .orderId(approval.getOrderId())
                .status("APPROVED")
                .message("Order approved successfully")
                .build();
    }

    @Override
    public void rejectOrder(RejectOrderCommand command) {
        Restaurant restaurant = repositoryPort.findById(new RestaurantId(command.getRestaurantId())).orElseThrow(() -> new RuntimeException("Restaurant not found"));
        OrderApproval approval = OrderApproval.builder()
                .orderId(command.getOrderId())
                .restaurantId(restaurant.getId())
                .build();

        OrderRejectedEvent rejectedEvent = domainService.rejectOrder(restaurant, approval, List.of(), command.getReason());
        publisherPort.publish(rejectedEvent);

        repositoryPort.saveApproval(approval);
    }
}