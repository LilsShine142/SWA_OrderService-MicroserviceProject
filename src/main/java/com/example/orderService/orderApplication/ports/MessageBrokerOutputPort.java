package com.example.orderService.orderApplication.ports;

public interface MessageBrokerOutputPort {
    void sendMessage(String topic, Object message);
}
