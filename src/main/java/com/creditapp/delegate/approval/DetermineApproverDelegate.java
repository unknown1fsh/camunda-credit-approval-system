package com.creditapp.delegate.approval;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

/**
 * Dynamic approver assignment delegate
 */
@Component("determineApproverDelegate")
@Slf4j
public class DetermineApproverDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String applicationId = (String) execution.getVariable("applicationId");
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> approvalDecision = (java.util.Map<String, Object>) execution.getVariable("approvalDecision");
        String approverLevel = approvalDecision != null && approvalDecision.containsKey("approverLevel")
                ? String.valueOf(approvalDecision.get("approverLevel"))
                : "MANAGER";
        Object slaHours = approvalDecision != null ? approvalDecision.get("slaHours") : null;
        if (slaHours != null) {
            execution.setVariable("slaHours", slaHours);
        }
        execution.setVariable("approverLevel", approverLevel);

        log.info("Determining approver for application {}: level={}, slaHours={}",
                applicationId, approverLevel, slaHours);

        // Determine assignee based on level (manager handles all approval levels)
        String assignee = "manager";

        execution.setVariable("taskAssignee", assignee);
        log.info("Assigned approver {} for application {}", assignee, applicationId);
    }
}
