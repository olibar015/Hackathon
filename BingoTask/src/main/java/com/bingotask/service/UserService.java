package com.bingotask.service;

import com.bingotask.dto.request.UpdateProfileRequest;
import com.bingotask.dto.response.AchievementResponse;
import com.bingotask.dto.response.UserProfileResponse;
import com.bingotask.exception.BadRequestException;
import com.bingotask.exception.ResourceNotFoundException;
import com.bingotask.model.User;
import com.bingotask.repository.UserRepository;
import com.bingotask.repository.UserTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserTaskRepository userTaskRepository;

    @Autowired
    private AchievementService achievementService;

    @Autowired
    private PasswordEncoder passwordEncoder;

  public UserProfileResponse getUserProfile(String username) {
    User user = userRepository.findByUsername(username)
      .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

    // Calculate completed tasks count
    Integer completedTasks = userTaskRepository.countByUser(user);

    // Get recent achievements (limit to 3 most recent)
    List<AchievementResponse> recentAchievements = achievementService.getRecentAchievements(user.getId(), 3);

    UserProfileResponse response = new UserProfileResponse();
    response.setId(user.getId());
    response.setUsername(user.getUsername());
    response.setEmail(user.getEmail());
    response.setAvatarUrl(user.getAvatarUrl());
    response.setLevel(user.getLevel());
    response.setXp(user.getXp());
    response.setXpToNextLevel(user.getXpToNextLevel());
    response.setTotalPoints(user.getTotalPoints());
    response.setCurrentStreak(user.getCurrentStreak());
    response.setBestStreak(user.getBestStreak());
    response.setCompletedTasks(completedTasks);
    response.setBingoLines(0); // You'll need to implement this
    response.setRecentAchievements(recentAchievements);
    response.setCreatedAt(user.getCreatedAt());

    return response;
  }

    public List<AchievementResponse> getUserAchievements(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        return achievementService.getUserAchievements(user.getId());
    }

    public Integer getCurrentStreak(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        return user.getCurrentStreak();
    }

    public Integer getBestStreak(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        return user.getBestStreak();
    }

    public UserProfileResponse updateProfile(String username, UpdateProfileRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        // Update allowed fields
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            // Check if email is already taken by another user
            if (!user.getEmail().equals(request.getEmail())) {
                boolean emailExists = userRepository.existsByEmail(request.getEmail());
                if (emailExists) {
                    throw new BadRequestException("Email is already in use by another user");
                }
                user.setEmail(request.getEmail());
            }
        }

        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }

        User updatedUser = userRepository.save(user);
        return getUserProfile(updatedUser.getUsername());
    }

    public Map<String, Object> getUserStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // Total users
        long totalUsers = userRepository.count();
        stats.put("totalUsers", totalUsers);

        // Active users (users with activity in last 7 days)
        LocalDate weekAgo = LocalDate.now().minusDays(7);
        long activeUsers = userRepository.countByLastActivityDateAfter(weekAgo);
        stats.put("activeUsers", activeUsers);

        // Users by level
        List<User> allUsers = userRepository.findAll();
        Map<Integer, Long> usersByLevel = allUsers.stream()
                .collect(Collectors.groupingBy(User::getLevel, Collectors.counting()));
        stats.put("usersByLevel", usersByLevel);

        // Average points per user
        double averagePoints = allUsers.stream()
                .mapToInt(User::getTotalPoints)
                .average()
                .orElse(0.0);
        stats.put("averagePoints", averagePoints);

        // Average streak
        double averageStreak = allUsers.stream()
                .mapToInt(User::getCurrentStreak)
                .average()
                .orElse(0.0);
        stats.put("averageStreak", averageStreak);

        // Total tasks completed
        long totalTasksCompleted = userTaskRepository.count();
        stats.put("totalTasksCompleted", totalTasksCompleted);

        // New users this week
        LocalDateTime weekStart = LocalDateTime.now().minusDays(7);
        long newUsersThisWeek = userRepository.countByCreatedAtAfter(weekStart);
        stats.put("newUsersThisWeek", newUsersThisWeek);

        return stats;
    }

    public void resetPassword(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Generate a random temporary password
        String tempPassword = generateTemporaryPassword();
        user.setPassword(passwordEncoder.encode(tempPassword));
        userRepository.save(user);

        // Send email with temporary password (implement email service)
        // emailService.sendPasswordResetEmail(user.getEmail(), tempPassword);
    }

    private String generateTemporaryPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        return password.toString();
    }

  public void updateUserRole(Long userId, User.Role role) {
    User user = userRepository.findById(userId)
      .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

    user.setRole(role);
    userRepository.save(user);
  }
}
