package com.adidas.product.worker.olapic.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class StringKafkaPublisher implements KafkaPublisher<String> {
    @Value("${kafka.topics.error}")
    private String errors;

    @Value("${kafka.topics.result}")
    private String result;

    @Value("${kafka.topics.launch}")
    private String launch;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void error(String message) {
        send(errors, message);
    }

    @Override
    public void result(String message) {
        send(result, message);
    }

    @Override
    public void launch(String message) {
        send(launch, message);
    }

    private void send(String topic, String message) {
        kafkaTemplate.send(topic, String.valueOf(System.currentTimeMillis()), message);
    }
}
