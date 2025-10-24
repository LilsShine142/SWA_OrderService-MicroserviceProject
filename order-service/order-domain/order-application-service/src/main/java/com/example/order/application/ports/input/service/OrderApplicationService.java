package com.example.order.application.ports.input.service;

import com.example.order.application.dto.*;
import com.example.order.application.dto.*;

/**
 * INPUT PORT Interface cho Use Cases
 *
 * Vị trí: order-application-service/ports/input/service
 *
 * Notes:
 * - This interface is the application's entry point
 * - REST Controller, GraphQL Resolver, gRPC Service can call it
 * - Domain is unaware of HTTP, REST, GraphQL...
 *
 */
public interface OrderApplicationService {

    /**
     * Use Case: Create order
     *
     */
    CreateOrderResponse createOrder(CreateOrderCommand command);

    /**
     * Use Case: Track order
     *
     */
    TrackOrderResponse trackOrder(TrackOrderQuery query);

    /**
     * Use Case: Cancel order
     *
     */
    CancelOrderResponse cancelOrder(CancelOrderCommand command);
}