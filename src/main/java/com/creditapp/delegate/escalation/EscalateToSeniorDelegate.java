package com.creditapp.delegate.escalation;

import com.creditapp.service.EscalationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

/**
 * Escalation to senior delegate
 */
@Component("escalateToSeniorDelegate")
@RequiredArgsConstructor
@Slf4j
public class EscalateToSeniorDelegate implements JavaDelegate {

    private final EscalationService escalationService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String applicationId = (String) execution.getVariable("applicationId");
        String currentLevel = (String) execution.getVariable("approverLevel");

        log.warn("Escalating application {} from {}", applicationId, currentLevel);

        // Determine next level
        String nextLevel = "SENIOR";
        if ("SENIOR".equals(currentLevel)) {
            nextLevel = "DIRECTOR";
        }

        execution.setVariable("approverLevel", nextLevel);
        execution.setVariable("escalated", true);
        execution.setVariable("escalatedFrom", currentLevel);
        execution.setVariable("escalatedTo", nextLevel);

        log.info("Application {} escalated to {}", applicationId, nextLevel);
    }
}
