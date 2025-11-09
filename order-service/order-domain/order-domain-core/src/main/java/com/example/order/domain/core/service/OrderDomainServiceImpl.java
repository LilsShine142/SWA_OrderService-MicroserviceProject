package com.example.order.domain.core.service;



import com.example.order.domain.core.entity.Order;
import com.example.order.domain.core.event.OrderCreatedEvent;
import com.example.order.domain.core.exception.OrderDomainException;

public class OrderDomainServiceImpl implements OrderDomainService {

    // (XÓA BỎ: private static final Logger LOG = LoggerFactory.getLogger(OrderDomainServiceImpl.class);)

    @Override
    public OrderCreatedEvent validateAndInitializeOrder(Order order) {
        // SỬA: Thay LOG.info bằng log.info (tên biến của Lombok)


        // 1. Khởi tạo Order (Gán ID, TrackingID, PENDING)
        order.initializeOrder();

        // 2. Xác thực quy tắc nghiệp vụ nội bộ (giá cả, trạng thái...)
        order.validateOrder();

        // 3. Giả lập kiểm tra chéo... (code giữ nguyên)

        // SỬA: Thay LOG.info bằng log.info

        // 4. Trả về sự kiện
        return (OrderCreatedEvent) order.getDomainEvents().stream()
                .filter(e -> e instanceof OrderCreatedEvent)
                .findFirst()
                .orElseThrow(() -> new OrderDomainException("Không tìm thấy OrderCreatedEvent sau khi khởi tạo."));
    }
}