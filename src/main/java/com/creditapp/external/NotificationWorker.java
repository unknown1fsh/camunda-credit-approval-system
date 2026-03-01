package com.creditapp.external;

import com.creditapp.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.ExternalTaskClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * External Task Worker for Notifications
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationWorker {

        @Value("${external-task.base-url:http://localhost:8080/engine-rest}")
        private String camundaEngineUrl;

        private final NotificationService notificationService;

        @EventListener(ApplicationReadyEvent.class)
        public void startWorker() {
                ExternalTaskClient client = ExternalTaskClient.create()
                                .baseUrl(camundaEngineUrl)
                                .asyncResponseTimeout(10000)
                                .build();

                client.subscribe("notification")
                                .lockDuration(60000) // 1 minute
                                .handler((externalTask, externalTaskService) -> {
                                        String applicationId = externalTask.getVariable("applicationId");
                                        String notificationType = externalTask.getVariable("notificationType");

                                        log.info("Processing notification for application {}: type={}",
                                                        applicationId, notificationType);

                                        try {
                                                String email = externalTask.getVariable("email");
                                                String phone = externalTask.getVariable("phone");
                                                String status = externalTask.getVariable("status");

                                                notificationService.notifyApplicationStatus(
                                                                applicationId, email, phone, applicationId, status);

                                                externalTaskService.complete(externalTask);
                                                log.info("Notification sent for application {}", applicationId);

                                        } catch (Exception e) {
                                                log.error("Error sending notification for {}: {}",
                                                                applicationId, e.getMessage());
                                                externalTaskService.handleFailure(externalTask,
                                                                e.getMessage(),
                                                                e.toString(),
                                                                externalTask.getRetries() - 1,
                                                                5000);
                                        }
                                })
                                .open();

                log.info("Notification Worker started and subscribed to 'notification' topic");
        }
}
