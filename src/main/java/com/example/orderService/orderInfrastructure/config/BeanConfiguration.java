package com.example.orderService.orderInfrastructure.config;

import com.example.orderService.orderApplication.command.CreateOrderCommandHandler;
import com.example.orderService.orderApplication.ports.OrderApplicationService;
import com.example.orderService.orderApplication.ports.OrderApplicationServiceImpl;
import com.example.orderService.orderApplication.ports.OrderEventPublisher;
import com.example.orderService.orderApplication.query.GetOrderQueryHandler;
import com.example.orderService.orderDomain.service.OrderDomainService;
import com.example.orderService.orderInfrastructure.persistence.adapter.OrderRepositoryAdapter;
import com.example.orderService.orderInfrastructure.persistence.mapper.OrderPersistenceMapper;
import com.example.orderService.orderInfrastructure.persistence.repository.OrderJpaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public OrderRepositoryAdapter orderRepositoryAdapter(OrderJpaRepository orderJpaRepository, OrderPersistenceMapper orderPersistenceMapper) {
        return new OrderRepositoryAdapter(orderJpaRepository, orderPersistenceMapper);
    }

    @Bean
    public OrderApplicationService orderApplicationService(
            OrderRepositoryAdapter orderRepositoryAdapter,
            OrderDomainService orderDomainService,
            OrderEventPublisher orderEventPublisher) {
        return new OrderApplicationServiceImpl(orderRepositoryAdapter, orderDomainService, orderEventPublisher);
    }
}