package com.bingotask.controller;

import com.bingotask.dto.request.CompleteTaskRequest;
import com.bingotask.dto.response.ApiResponse;
import com.bingotask.dto.response.BingoCardResponse;
import com.bingotask.dto.response.TaskResponse;
import com.bingotask.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getAllTasks(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "true") Boolean activeOnly) {
        return ResponseEntity.ok(taskService.getAllTasks(category, activeOnly));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @PostMapping("/complete")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> completeTask(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CompleteTaskRequest request) {
        String username = userDetails.getUsername();
        taskService.completeTask(username, request.getTaskId());

        return ResponseEntity.ok(new ApiResponse(true, "Task completed successfully"));
    }

    @GetMapping("/bingo-card")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BingoCardResponse> getCurrentBingoCard(
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        return ResponseEntity.ok(taskService.getBingoCard(username));
    }

    @GetMapping("/progress")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getUserProgress(
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        Map<String, Object> progress = taskService.getUserProgress(username);
        return ResponseEntity.ok(progress);
    }
}
