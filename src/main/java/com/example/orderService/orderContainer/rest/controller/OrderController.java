package com.example.orderService.orderContainer.rest.controller;

import com.example.orderService.orderApplication.command.CreateOrderCommand;
import com.example.orderService.orderApplication.command.CreateOrderCommandHandler;
import com.example.orderService.orderContainer.rest.dto.CreateOrderRestRequest;
import com.example.orderService.orderContainer.rest.mapper.OrderRestMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final CreateOrderCommandHandler commandHandler;
    private final OrderRestMapper restMapper;

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody @Validated CreateOrderRestRequest request) {
        log.info("Nhận yêu cầu tạo đơn hàng cho khách hàng: {}", request.getCustomerId());

        CreateOrderCommand command = restMapper.toCreateOrderCommandWithItems(request);
        commandHandler.handle(command);

        return ResponseEntity.ok("Đơn hàng được tạo thành công");
    }
}