package com.adidas.product.worker.olapic.service;

import com.adidas.product.worker.olapic.domain.ArticleModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.annotation.AfterWrite;
import org.springframework.batch.core.annotation.BeforeWrite;
import org.springframework.batch.core.annotation.OnWriteError;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CustomWriteListener implements ItemWriteListener<ArticleModel> {
    private static final Logger LOG = LoggerFactory.getLogger(CustomWriteListener.class);

    private final KafkaPublisher kafkaPublisher;

    public CustomWriteListener(final KafkaPublisher kafkaPublisher) {
        this.kafkaPublisher = kafkaPublisher;
    }

    @Override
    @BeforeWrite
    public void beforeWrite(List<? extends ArticleModel> items) {

    }

    @Override
    @AfterWrite
    public void afterWrite(List<? extends ArticleModel> items) {
        int count = Optional.ofNullable(items).map(List::size).orElse(0);
        kafkaPublisher.result(count + " items were written to xml");
    }

    @Override
    @OnWriteError
    public void onWriteError(Exception exception, List<? extends ArticleModel> items) {
        kafkaPublisher.error(exception);
    }
}
