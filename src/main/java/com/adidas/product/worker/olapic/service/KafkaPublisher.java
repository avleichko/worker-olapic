package com.adidas.product.worker.olapic.service;

public interface KafkaPublisher<T> {

    void error(T message);

    void result(T message);

    void launch(T message);

}
