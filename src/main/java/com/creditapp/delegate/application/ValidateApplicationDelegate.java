package com.creditapp.delegate.application;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Başvuru doğrulama delegate
 */
@Component("validateApplicationDelegate")
@Slf4j
public class ValidateApplicationDelegate implements JavaDelegate {

    @Value("${app.credit.min-age:18}")
    private int minAge;

    @Value("${app.credit.min-loan-amount:5000}")
    private double minLoanAmount;

    @Value("${app.credit.max-loan-amount:1000000}")
    private double maxLoanAmount;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String applicationId = (String) execution.getVariable("applicationId");
        log.info("Validating application: {}", applicationId);

        Number ageVar = (Number) execution.getVariable("age");
        Integer age = ageVar != null ? ageVar.intValue() : null;

        Number loanAmountVar = (Number) execution.getVariable("loanAmount");
        Double loanAmount = loanAmountVar != null ? loanAmountVar.doubleValue() : null;

        String employmentStatus = (String) execution.getVariable("employmentStatus");

        boolean isValid = true;
        StringBuilder errors = new StringBuilder();

        if (age == null || age < minAge) {
            isValid = false;
            errors.append("Age must be ").append(minAge).append(" or above. ");
        }

        if (loanAmount == null || loanAmount < minLoanAmount) {
            isValid = false;
            errors.append("Minimum loan amount is ").append((int) minLoanAmount).append(" TL. ");
        }

        if (loanAmount != null && loanAmount > maxLoanAmount) {
            isValid = false;
            errors.append("Maximum loan amount is ").append((int) maxLoanAmount).append(" TL. ");
        }

        if (employmentStatus == null || employmentStatus.isEmpty()) {
            isValid = false;
            errors.append("Employment status is required. ");
        }

        // Set results
        execution.setVariable("applicationValid", isValid);
        if (!isValid) {
            execution.setVariable("validationErrors", errors.toString());
            log.warn("Validation failed for application {}: {}", applicationId, errors);
        } else {
            log.info("Validation passed for application {}", applicationId);
        }
    }
}
