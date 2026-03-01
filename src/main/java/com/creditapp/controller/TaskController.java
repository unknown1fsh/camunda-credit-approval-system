package com.creditapp.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Camunda task yönetimi controller
 */
@Controller
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Slf4j
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public String listTasks(Model model, Authentication authentication) {
        String username = authentication.getName();

        // Get tasks assigned to user
        List<Task> assignedTasks = taskService.createTaskQuery()
                .taskAssignee(username)
                .orderByTaskCreateTime()
                .desc()
                .list();

        // Get tasks in candidate groups
        List<Task> candidateTasks = taskService.createTaskQuery()
                .taskCandidateUser(username)
                .orderByTaskCreateTime()
                .desc()
                .list();

        // Fetch applicationId for each task (Task has no processVariables property)
        Map<String, String> taskApplicationIds = new HashMap<>();
        for (Task t : assignedTasks) {
            try {
                Object appId = taskService.getVariable(t.getId(), "applicationId");
                taskApplicationIds.put(t.getId(), appId != null ? appId.toString() : "-");
            } catch (Exception e) {
                taskApplicationIds.put(t.getId(), "-");
            }
        }
        for (Task t : candidateTasks) {
            try {
                Object appId = taskService.getVariable(t.getId(), "applicationId");
                taskApplicationIds.put(t.getId(), appId != null ? appId.toString() : "-");
            } catch (Exception e) {
                taskApplicationIds.put(t.getId(), "-");
            }
        }

        model.addAttribute("assignedTasks", assignedTasks);
        model.addAttribute("candidateTasks", candidateTasks);
        model.addAttribute("taskApplicationIds", taskApplicationIds);
        return "tasks/task-list";
    }

    @GetMapping("/{taskId}")
    public String viewTask(@PathVariable String taskId, Model model) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        if (task == null) {
            return "redirect:/tasks?error=notfound";
        }

        Map<String, Object> variables = taskService.getVariables(taskId);
        model.addAttribute("task", task);
        model.addAttribute("variables", variables);

        // Determine which form to show based on task definition key
        String formView = determineFormView(task.getTaskDefinitionKey());
        return formView;
    }

    @PostMapping("/{taskId}/complete")
    public String completeTask(@PathVariable String taskId,
                              @RequestParam Map<String, String> formData,
                              Authentication authentication) {
        log.info("Completing task {} by user {}", taskId, authentication.getName());

        try {
            Map<String, Object> variables = new HashMap<>();
            for (Map.Entry<String, String> entry : formData.entrySet()) {
                String key = entry.getKey();
                if (!"approved".equals(key) && !"_csrf".equals(key)) {
                    variables.put(key, entry.getValue());
                }
            }
            variables.put("approvedBy", authentication.getName());

            // Convert approved string to boolean for BPMN condition
            if (formData.containsKey("approved")) {
                variables.put("approved", "true".equalsIgnoreCase(formData.get("approved")));
            }
            // Map comments to rejectionReason for compensation flow
            if ("false".equalsIgnoreCase(formData.get("approved"))) {
                variables.put("rejectionReason", formData.getOrDefault("comments", "Yönetici tarafından reddedildi"));
            }

            taskService.complete(taskId, variables);
            log.info("Task {} completed successfully", taskId);
            return "redirect:/tasks?success=true";
        } catch (Exception e) {
            log.error("Error completing task {}: {}", taskId, e.getMessage());
            return "redirect:/tasks/" + taskId + "?error=true";
        }
    }

    @PostMapping("/{taskId}/claim")
    public String claimTask(@PathVariable String taskId, Authentication authentication) {
        try {
            taskService.claim(taskId, authentication.getName());
            return "redirect:/tasks/" + taskId;
        } catch (Exception e) {
            log.error("Error claiming task {}: {}", taskId, e.getMessage());
            return "redirect:/tasks?error=claim";
        }
    }

    private String determineFormView(String taskDefinitionKey) {
        return switch (taskDefinitionKey) {
            case "Activity_ManagerApproval" -> "tasks/manager-approval";
            case "Activity_CreditAnalysis" -> "tasks/credit-analysis";
            case "Activity_RiskAnalysis" -> "tasks/risk-analysis";
            default -> "tasks/generic-task";
        };
    }
}
