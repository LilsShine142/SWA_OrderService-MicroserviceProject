package com.example.orderService.orderInfrastructure.messaging.kafka.producer;

@Component
public class KafkaOrderEventPublisher implements MessageBrokerOutputPort {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void sendMessage(String topic, Object message) {
        kafkaTemplate.send(topic, message);
    }
}
