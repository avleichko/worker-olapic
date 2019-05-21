package com.adidas.product.worker.olapic.service;

import com.adidas.product.worker.olapic.domain.FlatArticleMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.stereotype.Component;

@Component
public class CustomReaderListener implements ItemReadListener<FlatArticleMap> {
    private static final Logger LOG = LoggerFactory.getLogger(CustomReaderListener.class);

    private final KafkaPublisher<String> kafkaPublisher;

    public CustomReaderListener(final KafkaPublisher<String> kafkaPublisher) {
        this.kafkaPublisher = kafkaPublisher;
    }

    @Override
    public void beforeRead() {

    }

    @Override
    public void afterRead(FlatArticleMap item) {

    }

    @Override
    public void onReadError(Exception ex) {
        kafkaPublisher.error(ex.getMessage());
    }
}
