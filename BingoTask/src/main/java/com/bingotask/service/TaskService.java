package com.bingotask.service;

import com.bingotask.dto.request.CreateTaskRequest;
import com.bingotask.dto.request.TaskFilter;
import com.bingotask.dto.request.UpdateTaskRequest;
import com.bingotask.dto.response.BingoCardResponse;
import com.bingotask.dto.response.TaskResponse;
import com.bingotask.exception.BadRequestException;
import com.bingotask.exception.ResourceNotFoundException;
import com.bingotask.model.Task;
import com.bingotask.model.User;
import com.bingotask.model.UserTask;
import com.bingotask.repository.TaskRepository;
import com.bingotask.repository.UserRepository;
import com.bingotask.repository.UserTaskRepository;
import com.bingotask.repository.specification.TaskSpecification;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserTaskRepository userTaskRepository;

    @Autowired
    private BingoService bingoService;

    @Autowired
    private AchievementService achievementService;

    public List<Task> getTasksWithFilters(TaskFilter filter) {
        // Call static method using class name, not instance
        Specification<Task> spec = TaskSpecification.withFilters(
                filter.getActiveOnly(),
                filter.getCategory(),
                filter.getMinPoints(),
                filter.getMaxPoints(),
                filter.getUserId(),
                filter.getCompletedToday()
        );

        return ((JpaSpecificationExecutor<Task>) taskRepository).findAll(spec);
    }

    // Get user's today's tasks
    public List<Task> getUserTodayTasks(Long userId) {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = LocalDateTime.now().toLocalDate().atTime(23, 59, 59);

        Specification<Task> spec = (root, query, criteriaBuilder) -> {
            Join<Task, UserTask> userTasks = root.join("userTasks", JoinType.LEFT);

            Predicate userPredicate = criteriaBuilder.equal(
                    userTasks.get("user").get("id"), userId);
            Predicate datePredicate = criteriaBuilder.between(
                    userTasks.get("completedAt"), startOfDay, endOfDay);

            return criteriaBuilder.and(userPredicate, datePredicate);
        };

        return ((JpaSpecificationExecutor<Task>) taskRepository).findAll(spec);
    }

    public List<TaskResponse> getAllTasks(String category, Boolean activeOnly) {
        List<Task> tasks;

        if (category != null && activeOnly) {
            tasks = taskRepository.findByCategoryAndIsActiveTrue(category);
        } else if (category != null) {
            tasks = taskRepository.findByCategory(category);
        } else if (activeOnly) {
            tasks = taskRepository.findByIsActiveTrue();
        } else {
            tasks = taskRepository.findAll();
        }

        return tasks.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public void completeTask(String username, Long taskId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        // Check if task already completed today
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = LocalDateTime.now().toLocalDate().atTime(LocalTime.MAX);

        boolean alreadyCompleted = userTaskRepository.existsByUserAndTaskAndCompletedAtBetween(
                user, task, startOfDay, endOfDay);

        if (alreadyCompleted) {
            throw new BadRequestException("Task already completed today");
        }

        // Create UserTask
        UserTask userTask = new UserTask();
        userTask.setUser(user);
        userTask.setTask(task);
        userTask.setCompletedAt(LocalDateTime.now());
        userTask.setPointsEarned(task.getPoints());
        userTaskRepository.save(userTask);

        // Update user stats
        updateUserStats(user, task.getPoints());

        // Check for achievements
        achievementService.checkAndAwardAchievements(user);

        // Update BINGO card
        bingoService.updateBingoCard(user, task);
    }

    private void updateUserStats(User user, Integer points) {
        user.setTotalPoints(user.getTotalPoints() + points);
        user.setXp(user.getXp() + points);

        // Check for level up
        while (user.getXp() >= user.getXpToNextLevel()) {
            user.setLevel(user.getLevel() + 1);
            user.setXp(user.getXp() - user.getXpToNextLevel());
            user.setXpToNextLevel(user.getLevel() * 100); // Example formula
        }

        // Update streak
        updateStreak(user);

        userRepository.save(user);
    }

    private void updateStreak(User user) {
        LocalDate today = LocalDate.now();

        if (user.getLastActivityDate() == null) {
            user.setCurrentStreak(1);
        } else if (user.getLastActivityDate().plusDays(1).equals(today)) {
            user.setCurrentStreak(user.getCurrentStreak() + 1);
        } else if (user.getLastActivityDate().equals(today)) {
            // Already updated today
            return;
        } else {
            user.setCurrentStreak(1);
        }

        user.setLastActivityDate(today);

        if (user.getCurrentStreak() > user.getBestStreak()) {
            user.setBestStreak(user.getCurrentStreak());
        }
    }

    private TaskResponse convertToResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setPoints(task.getPoints());
        response.setCategory(task.getCategory());
        response.setIconUrl(task.getIconUrl());
        response.setPositionX(task.getPositionX());
        response.setPositionY(task.getPositionY());
        // Set other fields as needed
        return response;
    }
    // Add these methods to your existing TaskService class:

    public TaskResponse createTask(CreateTaskRequest request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPoints(request.getPoints());
        task.setCategory(request.getCategory());
        task.setPositionX(request.getPositionX());
        task.setPositionY(request.getPositionY());
        task.setIsActive(request.getIsActive());
        task.setCreatedAt(LocalDateTime.now());

        Task savedTask = taskRepository.save(task);
        return convertToResponse(savedTask);
    }

    public TaskResponse updateTask(Long id, UpdateTaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPoints(request.getPoints());
        task.setCategory(request.getCategory());
        task.setPositionX(request.getPositionX());
        task.setPositionY(request.getPositionY());
        task.setIsActive(request.getIsActive());
        task.setUpdatedAt(LocalDateTime.now());

        Task updatedTask = taskRepository.save(task);
        return convertToResponse(updatedTask);
    }

    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        taskRepository.delete(task);
    }

    public Map<String, Object> getTaskStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // Total tasks
        long totalTasks = taskRepository.count();
        stats.put("totalTasks", totalTasks);

        // Active tasks
        long activeTasks = taskRepository.countByIsActiveTrue();
        stats.put("activeTasks", activeTasks);

        // Tasks by category
        List<Object[]> categoryCounts = taskRepository.countTasksByCategory();
        Map<String, Long> tasksByCategory = new HashMap<>();
        for (Object[] row : categoryCounts) {
            tasksByCategory.put((String) row[0], (Long) row[1]);
        }
        stats.put("tasksByCategory", tasksByCategory);

        // Average points per task
        List<Task> allTasks = taskRepository.findAll();
        double averagePoints = allTasks.stream()
                .mapToInt(Task::getPoints)
                .average()
                .orElse(0.0);
        stats.put("averagePoints", averagePoints);

        // Most common point values
        Map<Integer, Long> pointsDistribution = allTasks.stream()
                .collect(Collectors.groupingBy(Task::getPoints, Collectors.counting()));
        stats.put("pointsDistribution", pointsDistribution);

        return stats;
    }

    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        return convertToResponse(task);
    }

    public BingoCardResponse getBingoCard(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Get user's active bingo card
        // This requires BingoService to have a method to get bingo card
        // For now, return null or implement it

        // Uncomment when BingoService has this method:
        // return bingoService.getBingoCardResponse(user.getId());

        // Temporary placeholder - you need to implement this
        throw new UnsupportedOperationException("getBingoCard not implemented yet");
    }

    public Map<String, Object> getUserProgress(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Map<String, Object> progress = new HashMap<>();

        // Basic user stats
        progress.put("level", user.getLevel());
        progress.put("xp", user.getXp());
        progress.put("xpToNextLevel", user.getXpToNextLevel());
        progress.put("totalPoints", user.getTotalPoints());
        progress.put("currentStreak", user.getCurrentStreak());
        progress.put("bestStreak", user.getBestStreak());

        // Tasks completed today
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = LocalDateTime.now().toLocalDate().atTime(23, 59, 59);
        long tasksToday = userTaskRepository.countByUserAndCompletedAtBetween(user, startOfDay, endOfDay);
        progress.put("tasksCompletedToday", tasksToday);

        // Total tasks completed
        long totalTasks = userTaskRepository.countByUser(user);
        progress.put("totalTasksCompleted", totalTasks);

        // Calculate completion percentage (example: out of active tasks)
        long activeTasksCount = taskRepository.countByIsActiveTrue();
        double completionPercentage = activeTasksCount > 0 ?
                (totalTasks * 100.0) / activeTasksCount : 0.0;
        progress.put("completionPercentage", completionPercentage);

        // Bingo progress
        // This requires BingoService methods
        // progress.put("bingoLines", bingoService.getCompletedLines(user.getId()));
        // progress.put("bingoCellsCompleted", bingoService.getCompletedCells(user.getId()));

        return progress;
    }

}