package com.example.payment.messaging.kafka.consumer;

import com.example.payment.ports.input.service.PaymentApplicationService;
import com.example.payment.dto.CreatePaymentCommand;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Component
public class OrderEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(OrderEventConsumer.class);

    private final PaymentApplicationService paymentApplicationService;
    private final ObjectMapper objectMapper;

    public OrderEventConsumer(PaymentApplicationService paymentApplicationService,
                              ObjectMapper objectMapper) {
        this.paymentApplicationService = paymentApplicationService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "order-created")
    public void consumeOrderCreated(String eventJson) {
        String orderIdStr = null;
        try {
            // 1. 📥 PARSE EVENT FROM ORDER SERVICE
            Map<String, Object> eventMap = objectMapper.readValue(eventJson, Map.class);
            Map<String, Object> orderData = (Map<String, Object>) eventMap.get("payload");

            if (orderData == null) {
                throw new IllegalArgumentException("Order data not found in event payload");
            }

            orderIdStr = (String) orderData.get("id");
            String customerIdStr = (String) orderData.get("customerId");
            Object totalAmountObj = orderData.get("totalAmount");
            BigDecimal totalAmount = convertToBigDecimal(totalAmountObj);

            logger.info("💰 Received order-created event - Order: {}, Amount: {}, Customer: {}",
                    orderIdStr, totalAmount, customerIdStr);

            // 2. 🔄 CONVERT TO COMMAND FOR APPLICATION LAYER
            // ✅ FIXED: Use constructor with parameters instead of setters
            UUID orderId = UUID.fromString(orderIdStr);
            UUID customerId = UUID.fromString(customerIdStr);

            CreatePaymentCommand command = new CreatePaymentCommand(
                    orderId,
                    customerId,
                    totalAmount
            );

            // 3. 🎯 CALL USE CASE THROUGH INPUT PORT
            paymentApplicationService.createPayment(command);

            logger.info("✅ Successfully sent payment creation request for order: {}", orderIdStr);

        } catch (IllegalArgumentException e) {
            logger.error("💥 UUID format error for order: {} - {}", orderIdStr, e.getMessage());
        } catch (Exception e) {
            logger.error("💥 Error processing order-created event for order: {}", orderIdStr, e);
        }
    }

    private BigDecimal convertToBigDecimal(Object amountObject) {
        try {
            if (amountObject instanceof Number) {
                return BigDecimal.valueOf(((Number) amountObject).doubleValue());
            } else if (amountObject instanceof String) {
                return new BigDecimal((String) amountObject);
            } else {
                throw new IllegalArgumentException("Unsupported amount format: " + amountObject.getClass());
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid amount format: " + amountObject, e);
        }
    }
}