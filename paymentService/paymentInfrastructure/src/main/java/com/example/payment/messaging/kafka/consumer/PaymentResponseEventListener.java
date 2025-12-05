package com.example.payment.messaging.kafka.consumer;

import com.example.payment.ports.input.service.PaymentApplicationService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PaymentResponseEventListener {

    private static final Logger logger = LoggerFactory.getLogger(PaymentResponseEventListener.class);
    private final PaymentApplicationService paymentApplicationService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "payment-response", groupId = "payment-service-group")
    public void listen(String message) {
        logger.info("Received payment response: {}", message);
        try {
            // Parse JSON message to Map<String, String>
            Map<String, String> params = objectMapper.readValue(message, new TypeReference<Map<String, String>>() {});
            // Call the service to handle callback
            paymentApplicationService.handleCallback(params);
            logger.info("Payment callback processed successfully");
        } catch (Exception e) {
            logger.error("Error processing payment response: {}", e.getMessage(), e);
        }
    }
}
