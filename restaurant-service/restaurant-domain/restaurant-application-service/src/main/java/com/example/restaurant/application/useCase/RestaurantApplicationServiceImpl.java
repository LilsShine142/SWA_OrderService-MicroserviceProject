package com.example.restaurant.application.useCase;

import com.example.restaurant.application.dto.request.ApproveOrderCommand;
import com.example.restaurant.application.dto.response.OrderApprovalResponse;
import com.example.restaurant.application.mapper.RestaurantDataMapper;
import com.example.restaurant.application.ports.input.service.RestaurantApplicationService;
import com.example.restaurant.application.ports.output.publisher.OrderApprovedEventPublisher;
import com.example.restaurant.application.ports.output.publisher.OrderRejectedEventPublisher;
import com.example.restaurant.application.ports.output.repository.MenuRepository;
import com.example.restaurant.application.ports.output.repository.OrderApprovalRepository;
import com.example.restaurant.domain.core.entity.MenuItem;
import com.example.restaurant.domain.core.entity.OrderApproval;
import com.example.restaurant.domain.core.exception.RestaurantDomainException;
import com.example.restaurant.domain.core.service.OrderApprovalDomainService;
import com.example.restaurant.domain.core.service.OrderApprovalDomainServiceImpl;
import com.example.restaurant.domain.core.valueobject.*;
import com.example.common_messaging.dto.event.OrderApprovedEvent;
import com.example.common_messaging.dto.event.OrderRejectedEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Validated
@RequiredArgsConstructor
public class RestaurantApplicationServiceImpl implements RestaurantApplicationService {

    private final RestaurantDataMapper mapper;
    private final OrderApprovalRepository approvalRepository;
    private final MenuRepository menuRepository;
    private final OrderApprovedEventPublisher approvedPublisher;
    private final OrderRejectedEventPublisher rejectedPublisher;

    // TẠO DOMAIN SERVICE THỦ CÔNG – KHÔNG DÙNG @Service TRONG DOMAIN
    private final OrderApprovalDomainService domainService = new OrderApprovalDomainServiceImpl();

    // ================================
    // 1. APPROVE ORDER (SAGA STEP)
    // ================================
    @Override
    @Transactional
    public OrderApprovalResponse approveOrder(ApproveOrderCommand command) {
        System.out.println("Restaurant Service nhận yêu cầu duyệt đơn hàng: orderId=" + command.getOrderId() + ", sagaId=" + command.getSagaId());

        ApprovalId sagaId = ApprovalId.of(command.getSagaId());

        // 1. IDEMPOTENCY CHECK – ĐẠT TIÊU CHÍ 8 (SAGA)
        if (approvalRepository.findBySagaId(sagaId).isPresent()) {
            System.out.println("SagaId " + sagaId.getValue() + " đã được xử lý (idempotency)");
            return createIdempotentResponse(sagaId);
        }

        // 2. MAP DTO → DOMAIN
        OrderApproval approval = mapper.approveOrderCommandToOrderApproval(command);

        // 3. VALIDATE BUSINESS RULES (ENTITY)
        approval.validateApproval();

        // 4. CHECK MENU AVAILABILITY
        validateMenuItemsAvailability(command.getItems());

        // 5. INITIALIZE SAGA STATE
        approval.initializeApproval(TrackingId.generate(), sagaId);

        // 6. SAVE FIRST TIME
        OrderApproval savedApproval = approvalRepository.save(approval);
        System.out.println("Lưu OrderApproval thành công: approvalId=" + savedApproval.getId().getValue());

        try {
            // 7. DECIDE: APPROVE OR REJECT
            if (allItemsAvailable(command.getItems())) {
                // DÙNG DOMAIN SERVICE → TẠO DOMAIN EVENT
                com.example.restaurant.domain.core.event.OrderApprovedEvent domainEvent =
                        domainService.validateAndInitiateApproval(savedApproval);

                savedApproval.setSagaStatus(SagaStatus.COMPLETED);
                System.out.println("Đơn hàng được duyệt → publish OrderApprovedEvent");

                // CHUYỂN DOMAIN EVENT → DTO EVENT (SAGA)
                OrderApprovedEvent dtoEvent = OrderApprovedEvent.builder()
                        .orderId(command.getOrderId())
                        .restaurantId(command.getRestaurantId())
                        .sagaId(command.getSagaId())
                        .build();

                approvedPublisher.publish(dtoEvent);  // ← DÙNG DTO
            } else {
                String reason = "Một số món ăn không khả dụng";

                com.example.restaurant.domain.core.event.OrderRejectedEvent domainEvent =
                        domainService.rejectOrderApproval(savedApproval, reason);

                savedApproval.setSagaStatus(SagaStatus.FAILED);
                System.out.println("Đơn hàng bị từ chối: " + reason);

                OrderRejectedEvent dtoEvent = OrderRejectedEvent.builder()
                        .orderId(command.getOrderId())
                        .restaurantId(command.getRestaurantId())
                        .sagaId(command.getSagaId())
                        .reason(reason)
                        .build();

                rejectedPublisher.publish(dtoEvent);  // ← DÙNG DTO
            }

            // 8. SAVE FINAL STATE
            OrderApproval finalApproval = approvalRepository.save(savedApproval);

            // 9. CLEAR DOMAIN EVENTS
            finalApproval.clearDomainEvents();

            return mapper.orderApprovalToResponse(finalApproval, "Order processed successfully");

        } catch (Exception e) {
            System.out.println("Lỗi xử lý SAGA: " + e.getMessage());
            e.printStackTrace();

            savedApproval.setSagaStatus(SagaStatus.FAILED);
            savedApproval.setNextRetryAt(Instant.now().plusSeconds(30));
            approvalRepository.save(savedApproval);

            // COMPENSATION EVENT (DTO)
            OrderRejectedEvent event = OrderRejectedEvent.builder()
                    .orderId(command.getOrderId())
                    .restaurantId(command.getRestaurantId())
                    .sagaId(command.getSagaId())
                    .reason("Lỗi hệ thống: " + e.getMessage())
                    .build();
            rejectedPublisher.publish(event);

            throw new RestaurantDomainException("Lỗi xử lý SAGA", e);
        }
    }

    // ================================
    // 2. REJECT ORDER (COMPENSATION)
    // ================================
    @Override
    @Transactional
    public void rejectOrder(UUID orderId, String reason) {
        System.out.println("Restaurant Service từ chối đơn hàng: orderId=" + orderId + ", reason=" + reason);

        OrderApproval approval = approvalRepository.findByOrderId(OrderId.of(orderId))
                .orElseGet(() -> {
                    OrderApproval newApproval = OrderApproval.builder()
                            .orderId(OrderId.of(orderId))
                            .restaurantId(RestaurantId.generate())
                            .build();
                    newApproval.initializeApproval(TrackingId.generate(), ApprovalId.generate());
                    return newApproval;
                });

        // DÙNG DOMAIN SERVICE → TẠO DOMAIN EVENT
        com.example.restaurant.domain.core.event.OrderRejectedEvent domainEvent =
                domainService.rejectOrderApproval(approval, reason);

        approval.setSagaStatus(SagaStatus.FAILED);
        OrderApproval saved = approvalRepository.save(approval);

        // CHUYỂN DOMAIN EVENT → DTO EVENT (SAGA)
        OrderRejectedEvent dtoEvent = OrderRejectedEvent.builder()
                .orderId(orderId)
                .restaurantId(saved.getRestaurantId().getValue())
                .sagaId(saved.getSagaId().getValue())
                .reason(reason)
                .build();

        rejectedPublisher.publish(dtoEvent);  // ← DÙNG DTO
        System.out.println("Đã publish OrderRejectedEvent (compensation) cho orderId=" + orderId);
    }

    // ================================
    // HELPER METHODS
    // ================================
    private void validateMenuItemsAvailability(List<ApproveOrderCommand.OrderItemDto> dtos) {
        for (ApproveOrderCommand.OrderItemDto dto : dtos) {
            MenuItem dbItem = menuRepository.findByProductId(ProductId.of(dto.getProductId()))
                    .orElseThrow(() -> new RestaurantDomainException(
                            "Không tìm thấy món ăn: " + dto.getProductId()));

            if (!dbItem.isAvailable()) {
                throw new RestaurantDomainException(
                        "Món ăn không khả dụng: " + dbItem.getName());
            }
        }
    }

    private boolean allItemsAvailable(List<ApproveOrderCommand.OrderItemDto> dtos) {
        return dtos.stream()
                .allMatch(dto -> menuRepository.findByProductId(ProductId.of(dto.getProductId()))
                        .map(MenuItem::isAvailable)
                        .orElse(false));
    }

    private OrderApprovalResponse createIdempotentResponse(ApprovalId sagaId) {
        OrderApproval existing = approvalRepository.findBySagaId(sagaId)
                .orElseThrow(() -> new RestaurantDomainException("Không tìm thấy approval"));
        return mapper.orderApprovalToResponse(existing, "Already processed");
    }
}

