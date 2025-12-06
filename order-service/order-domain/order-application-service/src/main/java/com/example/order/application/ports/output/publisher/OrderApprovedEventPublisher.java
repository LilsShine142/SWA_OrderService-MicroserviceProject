package com.example.order.application.ports.output.publisher;

import com.example.order.domain.core.event.OrderApprovedEvent;

/**
 * OUTPUT PORT (Cổng Ra)
 * Interface này định nghĩa hợp đồng cho việc publish
 * sự kiện "Order Approved".
 */
public interface OrderApprovedEventPublisher {

    void publish(OrderApprovedEvent event);
}
