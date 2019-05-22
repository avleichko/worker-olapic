package com.adidas.product.worker.olapic.controller;

import com.adidas.product.worker.schema.WorkerLaunch;
import com.adidas.product.worker.olapic.config.BatchConfiguration;
import com.adidas.product.worker.olapic.exception.InvalidParameterException;
import com.adidas.product.worker.olapic.service.KafkaPublisher;
import org.apache.commons.collections4.CollectionUtils;
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
import java.util.List;

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
    private KafkaPublisher kafkaPublisher;

    @KafkaHandler
    public void handleLaunch(final WorkerLaunch message) {
        try {
            jobLauncher.run(job, jobParameters(message));
        } catch (Exception e) {
            LOG.error("Exception has occurred", e);
            kafkaPublisher.error(e);
        }
    }

    @KafkaHandler(isDefault = true)
    public void acceptDefaultMessage(Object input) {
        LOG.info("Got unknown message '{}'", input);
    }

    private JobParameters jobParameters(WorkerLaunch input) {
        return new JobParametersBuilder()
                .addDate("date", new Date())
                .addParameter("locale", new JobParameter(resolveLocale(input.getLocale())))
                .addParameter("brand_code", new JobParameter(input.getBrand()))
                // TODO rid of inline as job parameter
                .addParameter("type", new JobParameter("inline"))
                .toJobParameters();
    }

    private String resolveLocale(List<String> list) {
        return CollectionUtils.emptyIfNull(list)
                .stream()
                .findFirst()
                .orElseThrow(() -> new InvalidParameterException("Locale must be present"));
    }
}
