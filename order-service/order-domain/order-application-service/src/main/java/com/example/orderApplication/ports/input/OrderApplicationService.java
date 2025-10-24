package com.example.orderApplication.ports.input;


// (Import các DTOs khác như TrackOrderQuery, TrackOrderResponse...)

/**
 * INPUT PORT Interface cho Use Cases [cite: 112]
 * Định nghĩa các hành động mà application có thể thực hiện.
 */
public interface OrderApplicationService {

    /**
     * Use Case: Tạo đơn hàng [cite: 130]
     */
    CreateOrderResponse createOrder(CreateOrderCommand command);

    /**
     * Use Case: Theo dõi đơn hàng [cite: 132]
     */
    // TrackOrderResponse trackOrder(TrackOrderQuery query);
}