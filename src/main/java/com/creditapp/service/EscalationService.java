package com.creditapp.service;

import com.creditapp.model.ApprovalDecision;
import com.creditapp.repository.ApprovalDecisionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Escalation yönetimi service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EscalationService {

    private final TaskService taskService;
    private final ApprovalDecisionRepository approvalDecisionRepository;
    private final NotificationService notificationService;

    /**
     * Task'ı üst seviyeye escalate et
     */
    @Transactional
    public void escalateTask(String taskId, String applicationId, String fromLevel,
            String toLevel) {
        log.warn("Escalating task {} from {} to {}",
                taskId, fromLevel, toLevel);

        // Get task
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            log.error("Task not found for escalation: {}", taskId);
            return;
        }

        // Determine new assignee based on escalation level
        String newAssignee = determineAssignee(toLevel);

        // Update task
        taskService.setAssignee(taskId, newAssignee);
        taskService.setPriority(taskId, 100); // High priority
        taskService.setVariable(taskId, "escalated", true);
        taskService.setVariable(taskId, "escalatedFrom", fromLevel);
        taskService.setVariable(taskId, "escalatedTo", toLevel);
        taskService.setVariable(taskId, "escalatedAt", LocalDateTime.now());

        // Record escalation decision
        ApprovalDecision decision = ApprovalDecision.builder()
                .applicationId(applicationId)
                .taskId(taskId)
                .approverLevel(fromLevel)
                .approver("SYSTEM")
                .decision(ApprovalDecision.DecisionType.ESCALATED)
                .escalated(true)
                .escalatedFrom(fromLevel)
                .escalatedTo(toLevel)
                .escalatedAt(LocalDateTime.now())
                .comments("SLA timeout - auto-escalated")
                .decidedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

        approvalDecisionRepository.save(decision);

        // Send notification
        String approverEmail = getApproverEmail(newAssignee);
        notificationService.notifyEscalation(
                approverEmail, applicationId, fromLevel, toLevel);

        log.info("Task {} escalated successfully to {}", taskId, toLevel);
    }

    /**
     * SLA kontrolü ve otomatik escalation
     */
    public void checkSLAAndEscalate(String taskId, String applicationId,
            String currentLevel, int hoursPassed) {
        log.info("Checking SLA for task {}: level={}, hours={}",
                taskId, currentLevel, hoursPassed);

        Map<String, Integer> slaThresholds = new HashMap<>();
        slaThresholds.put("MANAGER", 24);
        slaThresholds.put("SENIOR", 48);
        slaThresholds.put("DIRECTOR", 72);

        Integer threshold = slaThresholds.get(currentLevel);
        if (threshold != null && hoursPassed >= threshold) {
            String nextLevel = getNextEscalationLevel(currentLevel);
            escalateTask(taskId, applicationId, currentLevel, nextLevel);
        }
    }

    /**
     * Bir sonraki escalation seviyesini belirle
     */
    private String getNextEscalationLevel(String currentLevel) {
        return switch (currentLevel) {
            case "MANAGER" -> "SENIOR";
            case "SENIOR" -> "DIRECTOR";
            case "DIRECTOR" -> "BOARD";
            default -> "DIRECTOR";
        };
    }

    /**
     * Seviyeye göre assignee belirle
     */
    private String determineAssignee(String level) {
        return switch (level) {
            case "MANAGER" -> "manager";
            case "SENIOR" -> "senior";
            case "DIRECTOR" -> "director";
            case "BOARD" -> "board";
            default -> "admin";
        };
    }

    /**
     * Approver email adresini al
     */
    private String getApproverEmail(String assignee) {
        // Mock implementation - gerçek uygulamada user service'den al
        return assignee + "@creditapp.com";
    }

    /**
     * Escalation geçmişini al
     */
    public java.util.List<ApprovalDecision> getEscalationHistory(String applicationId) {
        return approvalDecisionRepository.findEscalatedDecisions(applicationId);
    }
}
