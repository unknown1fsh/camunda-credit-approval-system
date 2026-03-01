package com.creditapp.service;

import com.creditapp.exception.RiskScoreNotFoundException;
import com.creditapp.model.RiskScore;
import com.creditapp.repository.RiskScoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.engine.DecisionService;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Risk skorlama ve DMN evaluation service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RiskScoringService {

    private final RiskScoreRepository riskScoreRepository;
    private final DecisionService decisionService;

    /**
     * DMN kullanarak risk skorlaması yap
     */
    public Map<String, Object> evaluateRiskWithDMN(Map<String, Object> inputs) {
        log.info("Evaluating risk with DMN for inputs: {}", inputs);

        // Prepare variables
        VariableMap variables = Variables.createVariables()
                .putValue("creditHistory", inputs.get("creditHistory"))
                .putValue("debtToIncomeRatio", inputs.get("debtToIncomeRatio"))
                .putValue("loanAmount", inputs.get("loanAmount"))
                .putValue("employmentStatus", inputs.get("employmentStatus"))
                .putValue("age", inputs.get("age"));

        // Evaluate DMN decision table
        DmnDecisionTableResult result = decisionService
                .evaluateDecisionTableByKey("risk-scoring-decision", variables);

        if (result.isEmpty()) {
            log.warn("No matching rule found in DMN for inputs: {}", inputs);
            // Return default values
            return Map.of(
                    "riskCategory", "MEDIUM",
                    "riskScore", 50,
                    "recommendedAction", "REVIEW",
                    "maxLoanAmount", 100000.0
            );
        }

        Map<String, Object> output = result.getSingleResult().getEntryMap();
        log.info("DMN evaluation result: {}", output);

        return output;
    }

    /**
     * Risk skorunu kaydet
     */
    @Transactional
    public RiskScore saveRiskScore(RiskScore riskScore) {
        riskScore.setCreatedAt(LocalDateTime.now());
        riskScore.setEvaluatedAt(LocalDateTime.now());
        RiskScore saved = riskScoreRepository.save(riskScore);
        log.info("Saved risk score for application {}: score={}, category={}",
                riskScore.getApplicationId(), riskScore.getTotalScore(),
                riskScore.getCategory());
        return saved;
    }

    /**
     * Fraud skorunu güncelle
     */
    @Transactional
    public void updateFraudScore(String applicationId, Integer fraudScore,
            Boolean fraudDetected, String fraudIndicators) {
        RiskScore riskScore = riskScoreRepository.findByApplicationId(applicationId)
                .orElseThrow(() -> new RiskScoreNotFoundException(applicationId));

        riskScore.setFraudScore(fraudScore);
        riskScore.setFraudDetected(fraudDetected);
        riskScore.setFraudIndicators(fraudIndicators);
        riskScoreRepository.save(riskScore);

        log.info("Updated fraud score for application {}: score={}, detected={}",
                applicationId, fraudScore, fraudDetected);
    }

    /**
     * Analist skorlarını güncelle
     */
    @Transactional
    public void updateAnalystScores(String applicationId, String analystType,
            Integer score, String notes) {
        RiskScore riskScore = riskScoreRepository.findByApplicationId(applicationId)
                .orElseThrow(() -> new RiskScoreNotFoundException(applicationId));

        if ("CREDIT_ANALYST".equals(analystType)) {
            riskScore.setCreditAnalystScore(score);
            riskScore.setCreditAnalystNotes(notes);
        } else if ("RISK_ANALYST".equals(analystType)) {
            riskScore.setRiskAnalystScore(score);
            riskScore.setRiskAnalystNotes(notes);
        }

        riskScoreRepository.save(riskScore);
        log.info("Updated {} score for application {}: score={}",
                analystType, applicationId, score);
    }

    /**
     * Risk skorunu bul
     */
    public RiskScore findByApplicationId(String applicationId) {
        return riskScoreRepository.findByApplicationId(applicationId)
                .orElse(null);
    }
}
