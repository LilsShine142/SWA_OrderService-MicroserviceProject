package com.example.order.application.ports.output.publisher;


import com.example.order.domain.core.event.OrderCreatedEvent;

/**
 * OUTPUT PORT (Cổng Ra)
 * Interface này định nghĩa hợp đồng cho việc publish
 * sự kiện "Order Created" (để yêu cầu thanh toán).
 * Lớp Application CẦN interface này.
 * Lớp Infrastructure (order-messaging) sẽ IMPLEMENT interface này.
 */
public interface OrderCreatedPaymentRequestPublisher {

    /**
     * Publish sự kiện OrderCreatedEvent.
     * Lớp Infrastructure (Kafka) sẽ implement logic này.
     * @param orderCreatedEvent Sự kiện domain chứa thông tin Order
     */
    void publish(OrderCreatedEvent orderCreatedEvent);
}