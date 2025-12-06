package com.example.order.application.ports.output.publisher;

import com.example.order.domain.core.event.OrderCancelledEvent;

/**
 * OUTPUT PORT (Cổng Ra)
 * Interface này định nghĩa hợp đồng cho việc publish
 * sự kiện "Order Cancelled".
 */
public interface OrderCancelledEventPublisher {

    void publish(OrderCancelledEvent event);
}
