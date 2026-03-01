package com.creditapp.external;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.ExternalTaskClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * External Task Worker for Fraud Detection
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class FraudDetectionWorker {

    @Value("${external-task.base-url:http://localhost:8080/engine-rest}")
    private String camundaEngineUrl;

    private final Random random = new Random();

    @EventListener(ApplicationReadyEvent.class)
    public void startWorker() {
        ExternalTaskClient client = ExternalTaskClient.create()
                .baseUrl(camundaEngineUrl)
                .asyncResponseTimeout(10000)
                .build();

        client.subscribe("fraud-detection")
                .lockDuration(300000) // 5 minutes
                .handler((externalTask, externalTaskService) -> {
                    String applicationId = externalTask.getVariable("applicationId");

                    log.info("Processing fraud detection for application: {}", applicationId);

                    try {
                        // Simulate ML model call
                        Thread.sleep(3000);

                        // Mock fraud score (0-100)
                        int fraudScore = random.nextInt(100);
                        boolean fraudDetected = fraudScore > 85;

                        Map<String, Object> variables = new HashMap<>();
                        variables.put("fraudScore", fraudScore);
                        variables.put("fraudDetected", fraudDetected);

                        if (fraudDetected) {
                            log.warn("FRAUD DETECTED for application {}: score={}",
                                    applicationId, fraudScore);
                            // Throw BPMN error to trigger error boundary event
                            externalTaskService.handleBpmnError(externalTask,
                                    "FRAUD_DETECTED",
                                    "High fraud risk detected: score=" + fraudScore);
                        } else {
                            externalTaskService.complete(externalTask, variables);
                            log.info("Fraud check passed for {}: score={}",
                                    applicationId, fraudScore);
                        }

                    } catch (Exception e) {
                        log.error("Error processing fraud detection for {}: {}",
                                applicationId, e.getMessage());
                        externalTaskService.handleFailure(externalTask,
                                e.getMessage(),
                                e.toString(),
                                externalTask.getRetries() - 1,
                                5000);
                    }
                })
                .open();

        log.info("Fraud Detection Worker started and subscribed to 'fraud-detection' topic");
    }
}
