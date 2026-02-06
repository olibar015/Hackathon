package com.bingotask.service;

import com.bingotask.dto.response.BingoCardResponse;
import com.bingotask.dto.response.BingoCardTaskResponse;
import com.bingotask.exception.BadRequestException;
import com.bingotask.exception.ResourceNotFoundException;
import com.bingotask.model.*;
import com.bingotask.repository.BingoCardRepository;
import com.bingotask.repository.BingoCardTaskRepository;
import com.bingotask.repository.TaskRepository;
import com.bingotask.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class BingoService {

  @Autowired
  private BingoCardRepository bingoCardRepository;

  @Autowired
  private BingoCardTaskRepository bingoCardTaskRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private TaskRepository taskRepository;

  // Create or get active bingo card for user
  public BingoCardResponse getOrCreateBingoCard(String username) {
    User user = userRepository.findByUsername(username)
      .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    // Try to get active bingo card
    Optional<BingoCard> activeCard = bingoCardRepository.findByUserIdAndIsActiveTrue(user.getId());

    if (activeCard.isPresent()) {
      return convertToResponse(activeCard.get());
    } else {
      // Create new bingo card
      BingoCard newCard = createNewBingoCard(user);
      return convertToResponse(newCard);
    }
  }

  public BingoCard createNewBingoCard(User user) {
    // Check if user has too many active cards (should only have 1)
    List<BingoCard> userCards = bingoCardRepository.findByUserId(user.getId());
    userCards.stream()
      .filter(BingoCard::getIsActive)
      .forEach(card -> {
        card.setIsActive(false);
        bingoCardRepository.save(card);
      });

    // Create new bingo card
    BingoCard bingoCard = new BingoCard();
    bingoCard.setUser(user);
    bingoCard.setCardName("Bingo Card #" + (userCards.size() + 1));
    bingoCard.setIsActive(true);
    bingoCard.setIsCompleted(false);
    bingoCard.setIsApproved(false);

    BingoCard savedCard = bingoCardRepository.save(bingoCard);

    // Populate with random tasks
    populateBingoCardWithTasks(savedCard);

    return savedCard;
  }

  private void populateBingoCardWithTasks(BingoCard bingoCard) {
    List<Task> availableTasks = taskRepository.findByIsActiveTrue();

    if (availableTasks.size() < 25) {
      throw new BadRequestException("Not enough active tasks available. Need at least 25, have " + availableTasks.size());
    }

    // Shuffle tasks randomly
    Collections.shuffle(availableTasks);

    // Take first 25 tasks
    List<Task> selectedTasks = availableTasks.subList(0, 25);

    // Assign to 5x5 grid positions
    int taskIndex = 0;
    for (int x = 0; x < 5; x++) {
      for (int y = 0; y < 5; y++) {
        Task task = selectedTasks.get(taskIndex);
        BingoCardTask bingoCardTask = new BingoCardTask();
        bingoCardTask.setBingoCard(bingoCard);
        bingoCardTask.setTask(task);
        bingoCardTask.setPositionX(x);
        bingoCardTask.setPositionY(y);
        bingoCardTask.setIsCompleted(false);
        bingoCardTask.setIsVerified(false);

        bingoCardTaskRepository.save(bingoCardTask);
        taskIndex++;
      }
    }
  }

  // Complete a task on bingo card
  public BingoCardTaskResponse completeTask(String username, Long taskId) {
    User user = userRepository.findByUsername(username)
      .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    BingoCard bingoCard = bingoCardRepository.findByUserIdAndIsActiveTrue(user.getId())
      .orElseThrow(() -> new ResourceNotFoundException("No active bingo card found"));

    // Find the task in this bingo card
    BingoCardTask bingoCardTask = bingoCardTaskRepository.findByBingoCardIdAndTaskId(bingoCard.getId(), taskId)
      .orElseThrow(() -> new ResourceNotFoundException("Task not found in your bingo card"));

    if (bingoCardTask.getIsCompleted()) {
      throw new BadRequestException("Task already completed");
    }

    // Complete the task
    bingoCardTask.setIsCompleted(true);
    bingoCardTask.setCompletedDate(LocalDateTime.now());
    bingoCardTaskRepository.save(bingoCardTask);

    // Award points for task completion
    awardTaskCompletionPoints(user, bingoCardTask.getTask().getPoints());

    // Check if bingo card is now completed (has bingo lines)
    checkForBingoCompletion(bingoCard);

    return convertToTaskResponse(bingoCardTask);
  }

  private void awardTaskCompletionPoints(User user, Integer points) {
    if (points != null && points > 0) {
      user.setTotalPoints(user.getTotalPoints() + points);
      user.setXp(user.getXp() + points);
      checkLevelUp(user);
      userRepository.save(user);
    }
  }

  private void checkLevelUp(User user) {
    while (user.getXp() >= user.getXpToNextLevel()) {
      user.setLevel(user.getLevel() + 1);
      user.setXp(user.getXp() - user.getXpToNextLevel());
      user.setXpToNextLevel(user.getXpToNextLevel() + 100); // Increase required XP for next level
    }
  }

  private void checkForBingoCompletion(BingoCard bingoCard) {
    if (bingoCard.getIsCompleted()) {
      return; // Already completed
    }

    List<BingoCardTask> tasks = bingoCardTaskRepository.findByBingoCardId(bingoCard.getId());
    boolean hasBingo = checkForBingoLines(tasks);

    if (hasBingo) {
      bingoCard.setIsCompleted(true);
      bingoCard.setCompletedDate(LocalDateTime.now());
      bingoCardRepository.save(bingoCard);

      // Award bonus points for bingo completion
      awardBingoCompletionPoints(bingoCard.getUser());
    }
  }

  private boolean checkForBingoLines(List<BingoCardTask> tasks) {
    // Create a 5x5 grid of completion status
    boolean[][] grid = new boolean[5][5];
    for (BingoCardTask task : tasks) {
      if (task.getPositionX() < 5 && task.getPositionY() < 5) {
        grid[task.getPositionX()][task.getPositionY()] = task.getIsCompleted();
      }
    }

    // Check rows
    for (int x = 0; x < 5; x++) {
      boolean rowComplete = true;
      for (int y = 0; y < 5; y++) {
        if (!grid[x][y]) {
          rowComplete = false;
          break;
        }
      }
      if (rowComplete) return true;
    }

    // Check columns
    for (int y = 0; y < 5; y++) {
      boolean colComplete = true;
      for (int x = 0; x < 5; x++) {
        if (!grid[x][y]) {
          colComplete = false;
          break;
        }
      }
      if (colComplete) return true;
    }

    // Check diagonals
    boolean diag1 = true, diag2 = true;
    for (int i = 0; i < 5; i++) {
      if (!grid[i][i]) diag1 = false;
      if (!grid[i][4 - i]) diag2 = false;
    }

    return diag1 || diag2;
  }

  private void awardBingoCompletionPoints(User user) {
    user.setTotalPoints(user.getTotalPoints() + 500); // 500 points for bingo
    user.setXp(user.getXp() + 500);
    checkLevelUp(user);
    userRepository.save(user);
  }



  private void checkAndCompleteBingoCard(BingoCard bingoCard) {
    int completedTasks = bingoCardTaskRepository.countCompletedTasksByBingoCard(bingoCard);

    // Check for bingo lines (need at least 5 in a row)
    boolean hasBingo = checkForBingoLines(bingoCard);

    if (hasBingo && !bingoCard.getIsCompleted()) {
      bingoCard.setIsCompleted(true);
      bingoCardRepository.save(bingoCard);

      // Award points to user
      awardBingoCompletionPoints(bingoCard.getUser());
    }
  }

  private boolean checkForBingoLines(BingoCard bingoCard) {
    List<BingoCardTask> tasks = bingoCardTaskRepository.findByBingoCardId(bingoCard.getId());

    // Convert to 5x5 matrix
    boolean[][] completed = new boolean[5][5];
    for (BingoCardTask task : tasks) {
      completed[task.getPositionX()][task.getPositionY()] = task.getIsCompleted();
    }

    // Check rows
    for (int i = 0; i < 5; i++) {
      boolean rowComplete = true;
      for (int j = 0; j < 5; j++) {
        if (!completed[i][j]) {
          rowComplete = false;
          break;
        }
      }
      if (rowComplete) return true;
    }

    // Check columns
    for (int j = 0; j < 5; j++) {
      boolean colComplete = true;
      for (int i = 0; i < 5; i++) {
        if (!completed[i][j]) {
          colComplete = false;
          break;
        }
      }
      if (colComplete) return true;
    }

    // Check diagonals
    boolean diag1 = true, diag2 = true;
    for (int i = 0; i < 5; i++) {
      if (!completed[i][i]) diag1 = false;
      if (!completed[i][4 - i]) diag2 = false;
    }

    return diag1 || diag2;
  }

  // Approve a bingo card (for approvers/admins)
  public BingoCardResponse approveBingoCard(Long bingoCardId, String approverUsername) {
    User approver = userRepository.findByUsername(approverUsername)
      .orElseThrow(() -> new ResourceNotFoundException("Approver not found"));

    // Check if user has approver or admin role
    if (approver.getRole() != User.Role.APPROVER && approver.getRole() != User.Role.ADMIN) {
      throw new BadRequestException("Only approvers and admins can approve bingo cards");
    }

    BingoCard bingoCard = bingoCardRepository.findById(bingoCardId)
      .orElseThrow(() -> new ResourceNotFoundException("Bingo card not found"));

    if (!bingoCard.getIsCompleted()) {
      throw new BadRequestException("Bingo card must be completed before approval");
    }

    if (bingoCard.getIsApproved()) {
      throw new BadRequestException("Bingo card already approved");
    }

    bingoCard.setApprovedBy(approver);
    bingoCardRepository.save(bingoCard);

    // Award bonus points for approval
    awardApprovalBonus(bingoCard.getUser());

    return convertToResponse(bingoCard);
  }

  private void awardApprovalBonus(User user) {
    user.setTotalPoints(user.getTotalPoints() + 200); // 200 bonus points for approval
    userRepository.save(user);
  }

  // Get pending bingo cards for approval
  public List<BingoCardResponse> getPendingApprovals() {
    List<BingoCard> pendingCards = bingoCardRepository.findByIsCompletedTrueAndIsApprovedFalse();
    return pendingCards.stream()
      .map(this::convertToResponse)
      .collect(Collectors.toList());
  }

  // Verify a task (for approvers to verify task completion)
  public BingoCardTaskResponse verifyTask(Long bingoCardTaskId, String verifierUsername) {
    User verifier = userRepository.findByUsername(verifierUsername)
      .orElseThrow(() -> new ResourceNotFoundException("Verifier not found"));

    // Check if user has approver or admin role
    if (verifier.getRole() != User.Role.APPROVER && verifier.getRole() != User.Role.ADMIN) {
      throw new BadRequestException("Only approvers and admins can verify tasks");
    }

    BingoCardTask bingoCardTask = bingoCardTaskRepository.findById(bingoCardTaskId)
      .orElseThrow(() -> new ResourceNotFoundException("Bingo card task not found"));

    if (!bingoCardTask.getIsCompleted()) {
      throw new BadRequestException("Task must be completed before verification");
    }

    if (bingoCardTask.getIsVerified()) {
      throw new BadRequestException("Task already verified");
    }

    bingoCardTask.verify(verifier);
    bingoCardTaskRepository.save(bingoCardTask);

    return convertToTaskResponse(bingoCardTask);
  }

  // Get tasks pending verification
  public List<BingoCardTaskResponse> getPendingVerifications() {
    List<BingoCardTask> pendingTasks = bingoCardTaskRepository.findPendingVerifications();
    return pendingTasks.stream()
      .map(this::convertToTaskResponse)
      .collect(Collectors.toList());
  }

  // Convert entities to DTOs
  public BingoCardResponse convertToResponse(BingoCard bingoCard) {
    BingoCardResponse response = new BingoCardResponse();
    response.setId(bingoCard.getId());
    response.setUserId(bingoCard.getUser().getId());
    response.setCardName(bingoCard.getCardName());
    response.setActive(bingoCard.getIsActive());
    response.setCompleted(bingoCard.getIsCompleted());
    response.setApproved(bingoCard.getIsApproved());
    response.setCompletedDate(bingoCard.getCompletedDate());
    response.setApprovedBy(bingoCard.getApprovedBy() != null ?
      bingoCard.getApprovedBy().getUsername() : null);
    response.setApprovedDate(bingoCard.getApprovedDate());
    response.setCreatedAt(bingoCard.getCreatedAt());

    // Get tasks for this bingo card
    List<BingoCardTask> tasks = bingoCardTaskRepository.findByBingoCardId(bingoCard.getId());
    List<BingoCardTaskResponse> taskResponses = tasks.stream()
      .map(this::convertToTaskResponse)
      .collect(Collectors.toList());
    response.setTasks(taskResponses);

    return response;
  }

  private BingoCardTaskResponse convertToTaskResponse(BingoCardTask bingoCardTask) {
    BingoCardTaskResponse response = new BingoCardTaskResponse();
    response.setId(bingoCardTask.getId());
    response.setTaskId(bingoCardTask.getTask().getId());
    response.setTaskTitle(bingoCardTask.getTask().getTitle());
    response.setTaskDescription(bingoCardTask.getTask().getDescription());
    response.setTaskPoints(bingoCardTask.getTask().getPoints());
    response.setTaskCategory(bingoCardTask.getTask().getCategory());
    response.setPositionX(bingoCardTask.getPositionX());
    response.setPositionY(bingoCardTask.getPositionY());
    response.setCompleted(bingoCardTask.getIsCompleted());
    response.setVerified(bingoCardTask.getIsVerified());
    response.setCompletedDate(bingoCardTask.getCompletedDate());
    response.setVerifiedBy(bingoCardTask.getVerifiedBy() != null ?
      bingoCardTask.getVerifiedBy().getUsername() : null);
    response.setVerifiedDate(bingoCardTask.getVerifiedDate());

    return response;
  }


  private int countCompletedLines(List<BingoCardTask> tasks) {
    // Create a 5x5 grid of completion status
    boolean[][] grid = new boolean[5][5];
    for (BingoCardTask task : tasks) {
      if (task.getPositionX() < 5 && task.getPositionY() < 5) {
        grid[task.getPositionX()][task.getPositionY()] = task.getIsCompleted();
      }
    }

    int completedLines = 0;

    // Check rows
    for (int x = 0; x < 5; x++) {
      boolean rowComplete = true;
      for (int y = 0; y < 5; y++) {
        if (!grid[x][y]) {
          rowComplete = false;
          break;
        }
      }
      if (rowComplete) completedLines++;
    }

    // Check columns
    for (int y = 0; y < 5; y++) {
      boolean colComplete = true;
      for (int x = 0; x < 5; x++) {
        if (!grid[x][y]) {
          colComplete = false;
          break;
        }
      }
      if (colComplete) completedLines++;
    }

    // Check diagonals
    boolean diag1 = true, diag2 = true;
    for (int i = 0; i < 5; i++) {
      if (!grid[i][i]) diag1 = false;
      if (!grid[i][4 - i]) diag2 = false;
    }

    if (diag1) completedLines++;
    if (diag2) completedLines++;

    return completedLines;
  }

  private void awardBingoCompletionPoints(User user, int completedLines) {
    int points = completedLines * 100; // 100 points per completed line
    user.setTotalPoints(user.getTotalPoints() + points);
    user.setXp(user.getXp() + points);
    checkLevelUp(user);
    userRepository.save(user);
  }

  // Assign a specific task to user's bingo card
  public BingoCardTaskResponse assignTaskToUser(Long userId, Long taskId, Integer positionX, Integer positionY) {
    User user = userRepository.findById(userId)
      .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    Task task = taskRepository.findById(taskId)
      .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

    // Get or create active bingo card for user
    BingoCard bingoCard = bingoCardRepository.findByUserIdAndIsActiveTrue(user.getId())
      .or(() -> Optional.of(createNewBingoCard(user)))
      .orElseThrow(() -> new BadRequestException("Could not create bingo card"));

    // Check if position is already occupied
    if (positionX != null && positionY != null) {
      Optional<BingoCardTask> existingAtPosition = bingoCardTaskRepository.findByPosition(bingoCard, positionX, positionY);
      if (existingAtPosition.isPresent()) {
        throw new BadRequestException("Position (" + positionX + "," + positionY + ") is already occupied");
      }
    }

    // Check if task is already in bingo card
    Optional<BingoCardTask> existingTask = bingoCardTaskRepository.findByBingoCardIdAndTaskId(bingoCard.getId(), taskId);
    if (existingTask.isPresent()) {
      throw new BadRequestException("Task is already in the bingo card");
    }

    // Create bingo card task
    BingoCardTask bingoCardTask = new BingoCardTask();
    bingoCardTask.setBingoCard(bingoCard);
    bingoCardTask.setTask(task);

    // Set position if provided, otherwise find empty spot
    if (positionX != null && positionY != null) {
      bingoCardTask.setPositionX(positionX);
      bingoCardTask.setPositionY(positionY);
    } else {
      // Find first empty position (0,0) to (4,4)
      int[] emptyPosition = findEmptyPosition(bingoCard.getId());
      bingoCardTask.setPositionX(emptyPosition[0]);
      bingoCardTask.setPositionY(emptyPosition[1]);
    }

    bingoCardTask.setIsCompleted(false);
    bingoCardTask.setIsVerified(false);

    BingoCardTask savedTask = bingoCardTaskRepository.save(bingoCardTask);

    return convertToTaskResponse(savedTask);
  }

  private int[] findEmptyPosition(Long bingoCardId) {
    List<BingoCardTask> tasks = bingoCardTaskRepository.findByBingoCardId(bingoCardId);

    // Create a 5x5 grid to track occupied positions
    boolean[][] occupied = new boolean[5][5];
    for (BingoCardTask task : tasks) {
      if (task.getPositionX() < 5 && task.getPositionY() < 5) {
        occupied[task.getPositionX()][task.getPositionY()] = true;
      }
    }

    // Find first empty position
    for (int x = 0; x < 5; x++) {
      for (int y = 0; y < 5; y++) {
        if (!occupied[x][y]) {
          return new int[]{x, y};
        }
      }
    }

    throw new BadRequestException("Bingo card is full (25/25 tasks)");
  }

  // Remove task from user's bingo card
  public void removeTaskFromUser(Long userId, Long taskId) {
    User user = userRepository.findById(userId)
      .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    BingoCard bingoCard = bingoCardRepository.findByUserIdAndIsActiveTrue(user.getId())
      .orElseThrow(() -> new ResourceNotFoundException("No active bingo card found"));

    BingoCardTask bingoCardTask = bingoCardTaskRepository.findByBingoCardIdAndTaskId(bingoCard.getId(), taskId)
      .orElseThrow(() -> new ResourceNotFoundException("Task not found in user's bingo card"));

    bingoCardTaskRepository.delete(bingoCardTask);
  }

  // Replace a task in user's bingo card
  public BingoCardTaskResponse replaceTaskInBingoCard(Long userId, Long oldTaskId, Long newTaskId) {
    User user = userRepository.findById(userId)
      .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    Task newTask = taskRepository.findById(newTaskId)
      .orElseThrow(() -> new ResourceNotFoundException("New task not found"));

    BingoCard bingoCard = bingoCardRepository.findByUserIdAndIsActiveTrue(user.getId())
      .orElseThrow(() -> new ResourceNotFoundException("No active bingo card found"));

    // Find the existing task
    BingoCardTask existingTask = bingoCardTaskRepository.findByBingoCardIdAndTaskId(bingoCard.getId(), oldTaskId)
      .orElseThrow(() -> new ResourceNotFoundException("Old task not found in bingo card"));

    // Check if new task is already in bingo card
    Optional<BingoCardTask> duplicateTask = bingoCardTaskRepository.findByBingoCardIdAndTaskId(bingoCard.getId(), newTaskId);
    if (duplicateTask.isPresent()) {
      throw new BadRequestException("New task is already in the bingo card");
    }

    // Update the task
    existingTask.setTask(newTask);
    existingTask.setIsCompleted(false);  // Reset completion status
    existingTask.setIsVerified(false);   // Reset verification status
    existingTask.setCompletedDate(null);
    existingTask.setVerifiedBy(null);
    existingTask.setVerifiedDate(null);

    BingoCardTask updatedTask = bingoCardTaskRepository.save(existingTask);

    return convertToTaskResponse(updatedTask);
  }

  // Get all tasks assigned to a specific user
  public List<BingoCardTaskResponse> getAssignedTasks(Long userId) {
    User user = userRepository.findById(userId)
      .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    BingoCard bingoCard = bingoCardRepository.findByUserIdAndIsActiveTrue(user.getId())
      .orElseThrow(() -> new ResourceNotFoundException("No active bingo card found"));

    List<BingoCardTask> tasks = bingoCardTaskRepository.findByBingoCardId(bingoCard.getId());

    return tasks.stream()
      .map(this::convertToTaskResponse)
      .collect(Collectors.toList());
  }

}
