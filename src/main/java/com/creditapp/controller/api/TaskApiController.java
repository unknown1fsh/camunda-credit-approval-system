package com.creditapp.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Camunda görev REST API
 */
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Görevler", description = "Camunda task yönetimi API")
public class TaskApiController {

    private final TaskService taskService;

    @GetMapping
    @Operation(summary = "Kullanıcının görevlerini listele")
    public ResponseEntity<Map<String, Object>> listTasks(Authentication authentication) {
        String username = authentication.getName();

        List<Task> assignedTasks = taskService.createTaskQuery()
                .taskAssignee(username)
                .orderByTaskCreateTime()
                .desc()
                .list();

        List<Task> candidateTasks = taskService.createTaskQuery()
                .taskCandidateUser(username)
                .orderByTaskCreateTime()
                .desc()
                .list();

        Map<String, Object> result = new HashMap<>();
        result.put("assignedTasks", toTaskSummary(assignedTasks));
        result.put("candidateTasks", toTaskSummary(candidateTasks));

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{taskId}")
    @Operation(summary = "Görev detayı")
    public ResponseEntity<Map<String, Object>> getTask(@PathVariable String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> variables = taskService.getVariables(taskId);
        Map<String, Object> result = new HashMap<>();
        result.put("taskId", task.getId());
        result.put("name", task.getName());
        result.put("assignee", task.getAssignee());
        result.put("processInstanceId", task.getProcessInstanceId());
        result.put("variables", variables);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/{taskId}/complete")
    @Operation(summary = "Görevi tamamla")
    public ResponseEntity<Void> completeTask(@PathVariable String taskId,
                                              @RequestBody Map<String, Object> variables,
                                              Authentication authentication) {
        try {
            variables.put("approvedBy", authentication.getName());
            taskService.complete(taskId, variables);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{taskId}/claim")
    @Operation(summary = "Görevi üstlen")
    public ResponseEntity<Void> claimTask(@PathVariable String taskId, Authentication authentication) {
        try {
            taskService.claim(taskId, authentication.getName());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private List<Map<String, Object>> toTaskSummary(List<Task> tasks) {
        return tasks.stream()
                .map(t -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("taskId", t.getId());
                    m.put("name", t.getName());
                    m.put("assignee", t.getAssignee());
                    m.put("processInstanceId", t.getProcessInstanceId());
                    m.put("createTime", t.getCreateTime());
                    return m;
                })
                .collect(Collectors.toList());
    }
}
