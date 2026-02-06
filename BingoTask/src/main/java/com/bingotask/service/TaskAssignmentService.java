//package com.bingotask.service;
//
//import com.bingotask.dto.request.AssignTaskRequest;
//import com.bingotask.dto.request.VerifyTaskRequest;
//import com.bingotask.exception.BadRequestException;
//import com.bingotask.exception.ResourceNotFoundException;
//import com.bingotask.model.*;
//import com.bingotask.repository.TaskAssignmentRepository;
//import com.bingotask.repository.TaskRepository;
//import com.bingotask.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Service
//@Transactional
//public class TaskAssignmentService {
//
//  @Autowired
//  private TaskAssignmentRepository taskAssignmentRepository;
//
//  @Autowired
//  private UserRepository userRepository;
//
//  @Autowired
//  private TaskRepository taskRepository;
//
//  @Autowired
//  private TaskService taskService;
//
//  @Autowired
//  private AchievementService achievementService;
//
//  public TaskAssignment assignTask(String assignerUsername, AssignTaskRequest request) {
//    User assigner = userRepository.findByUsername(assignerUsername)
//      .orElseThrow(() -> new ResourceNotFoundException("Assigner not found"));
//
//    User assignee = userRepository.findByUsername(request.getAssigneeUsername())
//      .orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));
//
//    Task task = taskRepository.findById(request.getTaskId())
//      .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
//
//    // Check if already assigned and pending
//    boolean alreadyAssigned = taskAssignmentRepository.existsByAssigneeAndTaskAndStatus(
//      assignee, task, TaskAssignment.AssignmentStatus.PENDING);
//
//    if (alreadyAssigned) {
//      throw new BadRequestException("This task is already assigned to the user");
//    }
//
//    TaskAssignment assignment = new TaskAssignment();
//    assignment.setAssigner(assigner);
//    assignment.setAssignee(assignee);
//    assignment.setTask(task);
//    assignment.setDueDate(request.getDueDate());
//    assignment.setNotes(request.getNotes());
//    assignment.setStatus(TaskAssignment.AssignmentStatus.PENDING);
//
//    return taskAssignmentRepository.save(assignment);
//  }
//
//  public TaskAssignment completeAssignedTask(Long assignmentId, String proofUrl) {
//    TaskAssignment assignment = taskAssignmentRepository.findById(assignmentId)
//      .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
//
//    if (assignment.getStatus() != TaskAssignment.AssignmentStatus.PENDING) {
//      throw new BadRequestException("Task is not in pending status");
//    }
//
//    assignment.setStatus(TaskAssignment.AssignmentStatus.COMPLETED);
//    assignment.setCompletedAt(LocalDateTime.now());
//    assignment.setProofUrl(proofUrl);
//
//    return taskAssignmentRepository.save(assignment);
//  }
//
////  public TaskAssignment verifyTaskCompletion(Long assignmentId, String verifierUsername, VerifyTaskRequest request) {
////    TaskAssignment assignment = taskAssignmentRepository.findById(assignmentId)
////      .orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
////
////    User verifier = userRepository.findByUsername(verifierUsername)
////      .orElseThrow(() -> new ResourceNotFoundException("Verifier not found"));
////
////    // Check if verifier has APPROVER role
////    if (verifier.getRole() != User.Role.APPROVER && verifier.getRole() != User.Role.ADMIN) {
////      throw new BadRequestException("User does not have permission to verify tasks");
////    }
////
////    if (assignment.getStatus() != TaskAssignment.AssignmentStatus.COMPLETED) {
////      throw new BadRequestException("Task is not in completed status");
////    }
////
////    if (request.isApproved()) {
////      assignment.setStatus(TaskAssignment.AssignmentStatus.VERIFIED);
////      assignment.setVerifiedAt(LocalDateTime.now());
////      assignment.setVerifier(verifier);
////
////      // Create UserTask entry for the assignee
////      UserTask userTask = new UserTask();
////      userTask.setUser(assignment.getAssignee());
////      userTask.setTask(assignment.getTask());
////      userTask.setCompletedAt(assignment.getCompletedAt());
////      userTask.setPointsEarned(assignment.getTask().getPoints());
////
////      // TODO: Save UserTask and update user stats
////
////      // Update user stats
////      taskService.updateUserStats(assignment.getAssignee(), assignment.getTask().getPoints());
////
////      // Check for achievements
////      achievementService.checkAndAwardAchievements(assignment.getAssignee());
////
////    } else {
////      assignment.setStatus(TaskAssignment.AssignmentStatus.REJECTED);
////      assignment.setVerifiedAt(LocalDateTime.now());
////      assignment.setVerifier(verifier);
////    }
////
////    return taskAssignmentRepository.save(assignment);
////  }
//
//  public List<TaskAssignment> getAssignedTasks(String username) {
//    User user = userRepository.findByUsername(username)
//      .orElseThrow(() -> new ResourceNotFoundException("User not found"));
//
//    return taskAssignmentRepository.findByAssignee(user);
//  }
//
//  public List<TaskAssignment> getPendingVerifications(String username) {
//    User user = userRepository.findByUsername(username)
//      .orElseThrow(() -> new ResourceNotFoundException("User not found"));
//
//    // Only APPROVER and ADMIN can see pending verifications
//    if (user.getRole() != User.Role.APPROVER && user.getRole() != User.Role.ADMIN) {
//      throw new BadRequestException("User does not have permission to view pending verifications");
//    }
//
//    return taskAssignmentRepository.findPendingVerifications(user);
//  }
//
//  public List<TaskAssignment> getOverdueTasks(String username) {
//    User user = userRepository.findByUsername(username)
//      .orElseThrow(() -> new ResourceNotFoundException("User not found"));
//
//    LocalDate today = LocalDate.now();
//    return taskAssignmentRepository.findByAssigneeAndDueDateBeforeAndStatus(
//      user, today, TaskAssignment.AssignmentStatus.PENDING);
//  }
//}
