package com.adidas.task.worker.service;

import jdk.nashorn.internal.runtime.logging.Logger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.task.listener.TaskExecutionListener;
import org.springframework.cloud.task.listener.annotation.AfterTask;
import org.springframework.cloud.task.listener.annotation.BeforeTask;
import org.springframework.cloud.task.listener.annotation.FailedTask;
import org.springframework.cloud.task.repository.TaskExecution;
import org.springframework.cloud.task.repository.support.SimpleTaskRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.FileOutputStream;
import java.util.Date;
import java.util.Random;

@Service
@Slf4j
public class WorkerTaskRunnerService implements CommandLineRunner, TaskExecutionListener {

    @Value("${filePath}")
    private String path;

    @Autowired
    RestTemplate restTemplate;

    //Worker task may expose of 2 interfaces CommandLineRunner or Application Runner
    //
    public void run(String... args) throws Exception {
        log.info("worker creates file");
        int ARRAY_LENGTH = 555555555;

        byte[] byteArray = new byte[ARRAY_LENGTH];
        new Random(System.currentTimeMillis()).nextBytes(byteArray);

        String fullPath = path +"/"+ new Date().getTime() +".txt";
        try (FileOutputStream stream = new FileOutputStream(fullPath)) {
            stream.write(byteArray);
        }
    }

    //@BeforeTask can be done instead of implements TaskExecutionListener
    @Override
    public void onTaskStartup(TaskExecution taskExecution) {
        log.info("Task Listener before teask execution");
        final ResponseEntity<String> forEntity = restTemplate.getForEntity("http://localhost:8080/workerStart", String.class);
        log.info(forEntity.toString());
    }

    //@AfterTask can be done instead of implements TaskExecutionListener
    @Override
    public void onTaskEnd(TaskExecution taskExecution) {
        System.out.println("TAsk Listener On task end");
        log.info("Task Listener before teask execution");
        final ResponseEntity<String> forEntity = restTemplate.getForEntity("http://localhost:8080/workerEnd", String.class);
        log.info(forEntity.toString());
    }

    //@FailedTask can be done instead of implements TaskExecutionListener
    @Override
    public void onTaskFailed(TaskExecution taskExecution, Throwable throwable) {
        System.out.println("TAsk Listener on error");
    }
}
