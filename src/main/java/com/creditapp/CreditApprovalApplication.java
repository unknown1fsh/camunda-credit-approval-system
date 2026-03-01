package com.creditapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Credit Approval System - Boss Fight Edition
 *
 * Bu uygulama tüm ileri seviye Camunda BPM özelliklerini içeren
 * karmaşık bir kredi başvuru ve onay sistemidir.
 *
 * Özellikler:
 * - DMN Decision Tables
 * - Event Subprocesses & Compensation
 * - Message/Signal Events
 * - External Tasks & REST API
 * - Multi-Instance Patterns
 * - Timer Events & Escalation
 * - Dynamic Task Assignment
 * - Error Boundary Events
 */
@SpringBootApplication
@EnableScheduling
@EnableAsync
public class CreditApprovalApplication {

    public static void main(String[] args) {
        SpringApplication.run(CreditApprovalApplication.class, args);

        System.out.println("\n" +
                "╔══════════════════════════════════════════════════════════════╗\n" +
                "║   Credit Approval System - Boss Fight Edition Started!      ║\n" +
                "╠══════════════════════════════════════════════════════════════╣\n" +
                "║  Camunda Cockpit: http://localhost:8080/camunda             ║\n" +
                "║  H2 Console:      http://localhost:8080/h2-console          ║\n" +
                "║  API Docs:        http://localhost:8080/swagger-ui.html     ║\n" +
                "║  Application:     http://localhost:8080/applications/new    ║\n" +
                "║                                                              ║\n" +
                "║  Default User:    admin / admin                             ║\n" +
                "╚══════════════════════════════════════════════════════════════╝\n");
    }
}
