package com.adidas.product.worker.olapic.config;

import io.confluent.kafka.serializers.subject.TopicNameStrategy;

import java.util.Map;

@Deprecated
public class KafkaTopicNameStrategy extends TopicNameStrategy {

    @Override
    public void configure(final Map<String, ?> map) {
    }

    @Override
    public String getSubjectName(final String topic, final boolean isKey, final Object obj) {
        return topic + "-" + obj.getClass().getName();
    }
}
