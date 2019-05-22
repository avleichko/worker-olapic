package com.adidas.product.worker.olapic.service;

import com.adidas.product.worker.schema.WorkerLaunch;
import org.springframework.batch.core.JobExecution;

public interface KafkaPublisher {

    void error(Exception exception);

    void error(String errorMessage);

    void result(JobExecution execution, String jobFlow);

    void result(String jobFlow);

    void launch(WorkerLaunch message);

}
