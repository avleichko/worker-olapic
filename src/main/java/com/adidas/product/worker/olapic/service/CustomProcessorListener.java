package com.adidas.product.worker.olapic.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.annotation.AfterProcess;
import org.springframework.batch.core.annotation.BeforeProcess;
import org.springframework.batch.core.annotation.OnProcessError;
import org.springframework.stereotype.Component;

@Component
public class CustomProcessorListener implements ItemProcessListener<Object, Object> {
    private static final Logger LOG = LoggerFactory.getLogger(CustomProcessorListener.class);

    private final KafkaPublisher kafkaPublisher;

    public CustomProcessorListener(final KafkaPublisher kafkaPublisher) {
        this.kafkaPublisher = kafkaPublisher;
    }

    @Override
    @BeforeProcess
    public void beforeProcess(Object item) {

    }

    @Override
    @AfterProcess
    public void afterProcess(Object item, Object result) {

    }

    @Override
    @OnProcessError
    public void onProcessError(Object item, Exception e) {
        kafkaPublisher.error(e);
    }
}
