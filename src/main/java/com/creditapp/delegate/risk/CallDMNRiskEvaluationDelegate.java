package com.creditapp.delegate.risk;

import com.creditapp.service.RiskScoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * DMN risk evaluation delegate
 */
@Component("callDMNRiskEvaluationDelegate")
@RequiredArgsConstructor
@Slf4j
public class CallDMNRiskEvaluationDelegate implements JavaDelegate {

    private final RiskScoringService riskScoringService;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String applicationId = (String) execution.getVariable("applicationId");
        log.info("Evaluating risk with DMN for application: {}", applicationId);

        // Prepare inputs for DMN
        Map<String, Object> inputs = new HashMap<>();
        inputs.put("creditHistory", execution.getVariable("creditHistory"));
        inputs.put("debtToIncomeRatio", execution.getVariable("debtToIncomeRatio"));
        inputs.put("loanAmount", execution.getVariable("loanAmount"));
        inputs.put("employmentStatus", execution.getVariable("employmentStatus"));
        inputs.put("age", execution.getVariable("age"));

        // Call DMN decision table
        Map<String, Object> result = riskScoringService.evaluateRiskWithDMN(inputs);

        // Set process variables
        execution.setVariable("riskCategory", result.get("riskCategory"));
        execution.setVariable("riskScore", result.get("riskScore"));
        execution.setVariable("recommendedAction", result.get("recommendedAction"));
        execution.setVariable("maxLoanAmount", result.get("maxLoanAmount"));

        log.info("Risk evaluation result for {}: category={}, score={}",
                applicationId, result.get("riskCategory"), result.get("riskScore"));
    }
}
