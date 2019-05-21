package com.adidas.product.worker.olapic.controller;

import com.adidas.product.worker.olapic.config.BatchConfiguration;
import com.adidas.product.worker.olapic.service.KafkaPublisher;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@KafkaListener(containerFactory = "concurrentListener", topics = "worker-launcher")
public class KafkaFrontController {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaFrontController.class);

    @Autowired
    @Qualifier(BatchConfiguration.OLAPIC_LAUNCHER)
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier(BatchConfiguration.OLAPIC_JOB)
    private Job job;

    @Autowired
    private KafkaPublisher<String> kafkaPublisher;

    @KafkaHandler(isDefault = true)
    public void acceptDefaultMessage(String input) throws Exception {
        try {
            if (input == null) {
                return;
            }
            if (input.contains("run job")) {
                LOG.info("Running job '{}'", input);
                jobLauncher.run(job, jobParameters(input));
            } else {
                LOG.info("Got message '{}'", input);
            }
        } catch (Exception e) {
            LOG.error("Exception has occurred", e);
            kafkaPublisher.error(e.getMessage());
        }
    }

    private JobParameters jobParameters(String input) {
        return new JobParametersBuilder()
                .addDate("date", new Date())
                .addParameter("locale", new JobParameter(StringUtils.substringBetween(input, "locale", ";").trim()))
                .addParameter("brand_code", new JobParameter(StringUtils.substringBetween(input, "brand_code", ";").trim()))
                .addParameter("type", new JobParameter(StringUtils.substringBetween(input, "type", ";").trim()))
                .toJobParameters();
    }
}
