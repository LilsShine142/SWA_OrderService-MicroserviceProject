package com.example.order.application.ports.output.publisher;

import com.example.order.domain.core.event.OrderUpdatedEvent;

/**
 * OUTPUT PORT (Cổng Ra)
 * Interface này định nghĩa hợp đồng cho việc publish
 * sự kiện "Order Updated".
 */
public interface OrderUpdatedEventPublisher {

    void publish(OrderUpdatedEvent event);
}
