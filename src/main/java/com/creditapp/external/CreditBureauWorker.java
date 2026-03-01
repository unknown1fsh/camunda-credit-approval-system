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
 * External Task Worker for Credit Bureau API calls
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CreditBureauWorker {

    @Value("${external-task.base-url:http://localhost:8080/engine-rest}")
    private String camundaEngineUrl;

    private final Random random = new Random();

    @EventListener(ApplicationReadyEvent.class)
    public void startWorker() {
        ExternalTaskClient client = ExternalTaskClient.create()
                .baseUrl(camundaEngineUrl)
                .asyncResponseTimeout(10000)
                .build();

        client.subscribe("credit-bureau")
                .lockDuration(600000) // 10 minutes
                .handler((externalTask, externalTaskService) -> {
                    String applicationId = externalTask.getVariable("applicationId");
                    String customerId = externalTask.getVariable("customerId");

                    log.info("Processing credit bureau check for application: {}", applicationId);

                    try {
                        // Simulate external API call
                        Thread.sleep(2000);

                        // Mock credit history data (1-5 scale)
                        int creditHistory = random.nextInt(5) + 1;
                        int existingLoans = random.nextInt(5);

                        Map<String, Object> variables = new HashMap<>();
                        variables.put("creditHistory", creditHistory);
                        variables.put("existingLoans", existingLoans);
                        variables.put("creditBureauChecked", true);

                        externalTaskService.complete(externalTask, variables);
                        log.info("Credit bureau check completed for {}: history={}, loans={}",
                                applicationId, creditHistory, existingLoans);

                    } catch (Exception e) {
                        log.error("Error processing credit bureau check for {}: {}",
                                applicationId, e.getMessage());
                        Integer retries = externalTask.getRetries();
                        externalTaskService.handleFailure(externalTask,
                                e.getMessage(),
                                e.toString(),
                                retries != null ? retries - 1 : 2,
                                10000); // retry after 10 seconds
                    }
                })
                .open();

        log.info("Credit Bureau Worker started and subscribed to 'credit-bureau' topic");
    }
}
