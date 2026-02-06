//package com.bingotask.controller;
//
//import com.bingotask.dto.request.AssignTaskRequest;
//import com.bingotask.dto.request.VerifyTaskRequest;
//import com.bingotask.dto.response.ApiResponse;
//import com.bingotask.model.TaskAssignment;
//import com.bingotask.service.TaskAssignmentService;
//import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/assignments")
//@CrossOrigin(origins = "*")
//public class TaskAssignmentController {
//
//  @Autowired
//  private TaskAssignmentService taskAssignmentService;
//
//  @PostMapping("/assign")
//  @PreAuthorize("isAuthenticated()")
//  public ResponseEntity<TaskAssignment> assignTask(
//    @AuthenticationPrincipal UserDetails userDetails,
//    @Valid @RequestBody AssignTaskRequest request) {
//    String assignerUsername = userDetails.getUsername();
//    TaskAssignment assignment = taskAssignmentService.assignTask(assignerUsername, request);
//    return ResponseEntity.ok(assignment);
//  }
//
//  @GetMapping("/my-tasks")
//  @PreAuthorize("isAuthenticated()")
//  public ResponseEntity<List<TaskAssignment>> getMyAssignedTasks(
//    @AuthenticationPrincipal UserDetails userDetails) {
//    String username = userDetails.getUsername();
//    List<TaskAssignment> tasks = taskAssignmentService.getAssignedTasks(username);
//    return ResponseEntity.ok(tasks);
//  }
//
//  @PostMapping("/{assignmentId}/complete")
//  @PreAuthorize("isAuthenticated()")
//  public ResponseEntity<TaskAssignment> completeAssignedTask(
//    @PathVariable Long assignmentId,
//    @RequestParam(required = false) String proofUrl,
//    @AuthenticationPrincipal UserDetails userDetails) {
//    TaskAssignment assignment = taskAssignmentService.completeAssignedTask(assignmentId, proofUrl);
//    return ResponseEntity.ok(assignment);
//  }
//
//  @GetMapping("/pending-verifications")
//  @PreAuthorize("hasAnyRole('APPROVER', 'ADMIN')")
//  public ResponseEntity<List<TaskAssignment>> getPendingVerifications(
//    @AuthenticationPrincipal UserDetails userDetails) {
//    String username = userDetails.getUsername();
//    List<TaskAssignment> verifications = taskAssignmentService.getPendingVerifications(username);
//    return ResponseEntity.ok(verifications);
//  }
//
////  @PostMapping("/{assignmentId}/verify")
////  @PreAuthorize("hasAnyRole('APPROVER', 'ADMIN')")
////  public ResponseEntity<TaskAssignment> verifyTaskCompletion(
////    @PathVariable Long assignmentId,
////    @AuthenticationPrincipal UserDetails userDetails,
////    @Valid @RequestBody VerifyTaskRequest request) {
////    String verifierUsername = userDetails.getUsername();
////    TaskAssignment assignment = taskAssignmentService.verifyTaskCompletion(
////      assignmentId, verifierUsername, request);
////    return ResponseEntity.ok(assignment);
////  }
//
//  @GetMapping("/overdue")
//  @PreAuthorize("isAuthenticated()")
//  public ResponseEntity<List<TaskAssignment>> getOverdueTasks(
//    @AuthenticationPrincipal UserDetails userDetails) {
//    String username = userDetails.getUsername();
//    List<TaskAssignment> overdueTasks = taskAssignmentService.getOverdueTasks(username);
//    return ResponseEntity.ok(overdueTasks);
//  }
//}
