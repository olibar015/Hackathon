package com.bingotask.controller;

import com.bingotask.dto.request.CreateTaskRequest;
import com.bingotask.dto.request.UpdateTaskRequest;
import com.bingotask.dto.response.ApiResponse;
import com.bingotask.dto.response.TaskResponse;
import com.bingotask.service.TaskService;
import com.bingotask.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @PostMapping("/tasks")
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody CreateTaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.createTask(request));
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTaskRequest request) {
        return ResponseEntity.ok(taskService.updateTask(id, request));
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<ApiResponse> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok(new ApiResponse(true, "Task deleted successfully"));
    }

    @GetMapping("/tasks/stats")
    public ResponseEntity<Map<String, Object>> getTaskStatistics() {
        return ResponseEntity.ok(taskService.getTaskStatistics());
    }

    @GetMapping("/users/stats")
    public ResponseEntity<Map<String, Object>> getUserStatistics() {
        return ResponseEntity.ok(userService.getUserStatistics());
    }

    @PostMapping("/users/{userId}/reset-password")
    public ResponseEntity<ApiResponse> resetUserPassword(@PathVariable Long userId) {
        userService.resetPassword(userId);
        return ResponseEntity.ok(new ApiResponse(true, "Password reset email sent"));
    }
}
