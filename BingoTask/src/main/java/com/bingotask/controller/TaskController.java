package com.bingotask.controller;

import com.bingotask.dto.request.AssignTaskRequest;
import com.bingotask.dto.request.CompleteTaskRequest;
import com.bingotask.dto.response.ApiResponse;
import com.bingotask.dto.response.BingoCardResponse;
import com.bingotask.dto.response.BingoCardTaskResponse;
import com.bingotask.dto.response.TaskResponse;
import com.bingotask.exception.ResourceNotFoundException;
import com.bingotask.model.BingoCard;
import com.bingotask.model.Task;
import com.bingotask.model.User;
import com.bingotask.repository.BingoCardRepository;
import com.bingotask.repository.TaskRepository;
import com.bingotask.repository.UserRepository;
import com.bingotask.service.BingoService;
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
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    @Autowired
    private TaskService taskService;
    @Autowired
    private BingoService bingoService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BingoCardRepository bingoCardRepository;
    @Autowired
    private TaskRepository taskRepository;


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

//    @PostMapping("/complete")
//    @PreAuthorize("isAuthenticated()")
//    public ResponseEntity<ApiResponse> completeTask(
//            @AuthenticationPrincipal UserDetails userDetails,
//            @Valid @RequestBody CompleteTaskRequest request) {
//        String username = userDetails.getUsername();
//        taskService.completeTask(username, request.getTaskId());
//
//        return ResponseEntity.ok(new ApiResponse(true, "Task completed successfully"));
//    }

  @GetMapping("/bingo-card")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<BingoCardResponse> getCurrentBingoCard(
    @AuthenticationPrincipal UserDetails userDetails) {
    String username = userDetails.getUsername();
    return ResponseEntity.ok(taskService.getBingoCard(username));
  }

  @PostMapping("/bingo-card/{taskId}/complete")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<BingoCardTaskResponse> completeBingoCardTask(
    @PathVariable Long taskId,
    @AuthenticationPrincipal UserDetails userDetails) {
    String username = userDetails.getUsername();
    return ResponseEntity.ok(bingoService.completeTask(username, taskId));
  }

  @GetMapping("/bingo-card/pending-approvals")
  @PreAuthorize("hasAnyRole('APPROVER', 'ADMIN')")
  public ResponseEntity<List<BingoCardResponse>> getPendingApprovals() {
    return ResponseEntity.ok(bingoService.getPendingApprovals());
  }

  @PostMapping("/bingo-card/{bingoCardId}/approve")
  @PreAuthorize("hasAnyRole('APPROVER', 'ADMIN')")
  public ResponseEntity<BingoCardResponse> approveBingoCard(
    @PathVariable Long bingoCardId,
    @AuthenticationPrincipal UserDetails userDetails) {
    String username = userDetails.getUsername();
    return ResponseEntity.ok(bingoService.approveBingoCard(bingoCardId, username));
  }

  @GetMapping("/bingo-card/pending-verifications")
  @PreAuthorize("hasAnyRole('APPROVER', 'ADMIN')")
  public ResponseEntity<List<BingoCardTaskResponse>> getPendingVerifications() {
    return ResponseEntity.ok(bingoService.getPendingVerifications());
  }

  @PostMapping("/bingo-card/task/{bingoCardTaskId}/verify")
  @PreAuthorize("hasAnyRole('APPROVER', 'ADMIN')")
  public ResponseEntity<BingoCardTaskResponse> verifyTask(
    @PathVariable Long bingoCardTaskId,
    @AuthenticationPrincipal UserDetails userDetails) {
    String username = userDetails.getUsername();
    return ResponseEntity.ok(bingoService.verifyTask(bingoCardTaskId, username));
  }

  @PostMapping("/bingo-card/reset")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<BingoCardResponse> resetBingoCard(
    @AuthenticationPrincipal UserDetails userDetails) {
    String username = userDetails.getUsername();

    User user = userRepository.findByUsername(username)
      .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    // Deactivate current active card
    Optional<BingoCard> activeCard = bingoCardRepository.findByUserIdAndIsActiveTrue(user.getId());
    if (activeCard.isPresent()) {
      BingoCard card = activeCard.get();
      card.setIsActive(false);
      bingoCardRepository.save(card);
    }

    // Create new bingo card
    BingoCard newCard = bingoService.createNewBingoCard(user);
    return ResponseEntity.ok(bingoService.convertToResponse(newCard));
  }

  // Assign task to user (Admin/Approver only)
  @PostMapping("/assign")
  @PreAuthorize("hasAnyRole('ADMIN', 'APPROVER')")
  public ResponseEntity<BingoCardTaskResponse> assignTaskToUser(
    @Valid @RequestBody AssignTaskRequest request) {
    return ResponseEntity.ok(bingoService.assignTaskToUser(
      request.getUserId(),
      request.getTaskId(),
      request.getPositionX(),
      request.getPositionY()));
  }

  // Remove task from user (Admin/Approver only)
  @DeleteMapping("/assign/{userId}/{taskId}")
  @PreAuthorize("hasAnyRole('ADMIN', 'APPROVER')")
  public ResponseEntity<ApiResponse> removeTaskFromUser(
    @PathVariable Long userId,
    @PathVariable Long taskId) {
    bingoService.removeTaskFromUser(userId, taskId);
    return ResponseEntity.ok(new ApiResponse(true, "Task removed from user's bingo card"));
  }

  // Replace task in user's bingo card (Admin/Approver only)
  @PutMapping("/assign/replace")
  @PreAuthorize("hasAnyRole('ADMIN', 'APPROVER')")
  public ResponseEntity<BingoCardTaskResponse> replaceTaskInBingoCard(
    @RequestParam Long userId,
    @RequestParam Long oldTaskId,
    @RequestParam Long newTaskId) {
    return ResponseEntity.ok(bingoService.replaceTaskInBingoCard(userId, oldTaskId, newTaskId));
  }

  // Get all tasks assigned to a user (Admin/Approver only)
  @GetMapping("/assign/user/{userId}")
  @PreAuthorize("hasAnyRole('ADMIN', 'APPROVER')")
  public ResponseEntity<List<BingoCardTaskResponse>> getAssignedTasks(@PathVariable Long userId) {
    return ResponseEntity.ok(bingoService.getAssignedTasks(userId));
  }

  // User can request specific tasks (optional)
  @PostMapping("/request/{taskId}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ApiResponse> requestTaskAssignment(
    @PathVariable Long taskId,
    @AuthenticationPrincipal UserDetails userDetails) {
    // Get current user
    String username = userDetails.getUsername();
    User user = userRepository.findByUsername(username)
      .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    // Check if task exists and is active
    Task task = taskRepository.findByIdAndIsActiveTrue(taskId)
      .orElseThrow(() -> new ResourceNotFoundException("Task not found or inactive"));

    // Here you could implement a notification system or approval workflow
    // For now, just assign it automatically
    bingoService.assignTaskToUser(user.getId(), taskId, null, null);

    return ResponseEntity.ok(new ApiResponse(true, "Task requested and assigned"));
  }

//  @GetMapping("/categories/{category}")
//  public ResponseEntity<List<TaskResponse>> getTasksByCategory(@PathVariable String category) {
//    List<Task> tasks = taskRepository.findByCategoryAndIsActiveTrue(category);
//    List<TaskResponse> responses = tasks.stream()
//      .map(this::convertToResponse)
//      .collect(Collectors.toList());
//    return ResponseEntity.ok(responses);
//  }
//
//  @GetMapping("/categories")
//  public ResponseEntity<List<String>> getAllCategories() {
//    List<String> categories = taskRepository.findDistinctCategories();
//    return ResponseEntity.ok(categories);
//  }
}
