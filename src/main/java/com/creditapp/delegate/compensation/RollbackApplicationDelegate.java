package com.creditapp.delegate.compensation;

import com.creditapp.service.CompensationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

/**
 * Rollback/Compensation delegate
 */
@Component("rollbackApplicationDelegate")
@RequiredArgsConstructor
@Slf4j
public class RollbackApplicationDelegate implements JavaDelegate {

    private final CompensationService compensationService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String applicationId = (String) execution.getVariable("applicationId");
        String reason = (String) execution.getVariable("rejectionReason");

        if (reason == null || reason.isEmpty()) {
            reason = "Application rejected or cancelled";
        }

        log.warn("Executing rollback for application {}: {}", applicationId, reason);

        String email = (String) execution.getVariable("email");
        String phone = (String) execution.getVariable("phone");

        compensationService.executeFullCompensation(applicationId, reason, email, phone);

        log.info("Rollback completed for application {}", applicationId);
    }
}
