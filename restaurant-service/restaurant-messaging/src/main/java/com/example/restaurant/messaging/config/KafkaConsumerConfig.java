//package com.example.restaurant.messaging.config;
//
//import com.example.restaurant.domain.core.event.PaymentCompletedEvent;
//import com.example.restaurant.domain.core.event.PaymentFailedEvent;
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
//import org.springframework.kafka.core.ConsumerFactory;
//import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
//import org.springframework.kafka.support.serializer.JsonDeserializer;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class KafkaConsumerConfig {
//
//    private Map<String, Object> baseProps(String groupId) {
//        Map<String, Object> props = new HashMap<>();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
//        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
//        return props;
//    }
//
//    // PaymentCompletedEvent
//    public ConsumerFactory<String, PaymentCompletedEvent> paymentCompletedConsumerFactory() {
//        return new DefaultKafkaConsumerFactory<>(
//                baseProps("restaurant-service-payment-completed"),
//                new StringDeserializer(),
//                new JsonDeserializer<>(PaymentCompletedEvent.class, false)
//        );
//    }
//
//    public ConcurrentKafkaListenerContainerFactory<String, PaymentCompletedEvent> paymentCompletedKafkaListenerContainerFactory() {
//        ConcurrentKafkaListenerContainerFactory<String, PaymentCompletedEvent> factory =
//                new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(paymentCompletedConsumerFactory());
//        return factory;
//    }
//
//    // PaymentFailedEvent
//    public ConsumerFactory<String, PaymentFailedEvent> paymentFailedConsumerFactory() {
//        return new DefaultKafkaConsumerFactory<>(
//                baseProps("restaurant-service-payment-failed"),
//                new StringDeserializer(),
//                new JsonDeserializer<>(PaymentFailedEvent.class, false)
//        );
//    }
//
//    public ConcurrentKafkaListenerContainerFactory<String, PaymentFailedEvent> paymentFailedKafkaListenerContainerFactory() {
//        ConcurrentKafkaListenerContainerFactory<String, PaymentFailedEvent> factory =
//                new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(paymentFailedConsumerFactory());
//        return factory;
//    }
//}
