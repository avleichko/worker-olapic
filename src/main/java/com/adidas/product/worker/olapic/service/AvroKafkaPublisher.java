package com.adidas.product.worker.olapic.service;

import com.adidas.product.worker.schema.ResultSchema;
import com.adidas.product.worker.schema.WorkerFailure;
import com.adidas.product.worker.schema.WorkerLaunch;
import com.adidas.product.worker.olapic.config.KafkaConfiguration;
import org.apache.avro.specific.SpecificRecord;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Service
public class AvroKafkaPublisher implements KafkaPublisher {
    @Value("${kafka.topics.error}")
    private String errors;

    @Value("${kafka.topics.result}")
    private String result;

    @Value("${kafka.topics.launch}")
    private String launch;

    @Autowired
    private KafkaTemplate<String, SpecificRecord> kafkaTemplate;

    @Override
    public void error(Exception message) {
        WorkerFailure failure = new WorkerFailure();
        failure.setException(message.getMessage());
        // TODO just for fun or other information about failing, java stacktrace should be logged
        failure.setStacktrace(Arrays.toString(message.getStackTrace()));
        failure.setConsumer(KafkaConfiguration.OLAPIC);
        failure.setMillis(Clock.systemUTC().millis());
        failure.setId(UUID.randomUUID().toString());
        send(errors, failure);
    }

    @Override
    public void error(String errorMessage) {
        WorkerFailure failure = new WorkerFailure();
        failure.setException(errorMessage);
        // TODO just for fun or other information about failing, java stacktrace should be logged
        failure.setConsumer(KafkaConfiguration.OLAPIC);
        failure.setMillis(Clock.systemUTC().millis());
        failure.setId(UUID.randomUUID().toString());
    }


    @Override
    public void result(JobExecution execution, String jobFlow) {
        ResultSchema result = new ResultSchema();
        result.setConsumer(KafkaConfiguration.OLAPIC);
        result.setMillis(Clock.systemUTC().millis());
        result.setId(UUID.randomUUID().toString());
        result.setJobExecution(Optional.ofNullable(execution)
                .map(JobExecution::toString).orElse(null));
        result.setJobFlow(jobFlow);

        send(this.result, result);
    }

    @Override
    public void result(String jobFlow) {
        result(null, jobFlow);
    }


    @Override
    public void launch(WorkerLaunch message) {
        send(launch, message);
    }

    private void send(String topic, SpecificRecord message) {
        kafkaTemplate.send(topic, String.valueOf(System.currentTimeMillis()), message);
    }
}
