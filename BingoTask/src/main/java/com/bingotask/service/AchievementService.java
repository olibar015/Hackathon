package com.bingotask.service;

import com.bingotask.dto.response.AchievementProgressResponse;
import com.bingotask.dto.response.AchievementResponse;
import com.bingotask.exception.ResourceNotFoundException;
import com.bingotask.model.*;
import com.bingotask.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AchievementService {

    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private AchievementDefinitionRepository achievementDefinitionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserTaskRepository userTaskRepository;

    @Autowired
    private BingoCardRepository bingoCardRepository;

    public List<AchievementResponse> getUserAchievements(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        List<Achievement> achievements = achievementRepository.findByUserOrderByEarnedAtDesc(user);

        return achievements.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<AchievementProgressResponse> getAchievementProgress(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        List<AchievementDefinition> definitions = achievementDefinitionRepository.findByIsActiveTrue();
        List<Achievement> earnedAchievements = achievementRepository.findByUser(user);

        List<AchievementProgressResponse> progressList = new ArrayList<>();

        for (AchievementDefinition definition : definitions) {
            AchievementProgressResponse progress = new AchievementProgressResponse();
            progress.setAchievementId(definition.getId());
            progress.setName(definition.getName());
            progress.setDescription(definition.getDescription());
            progress.setIconUrl(definition.getIconUrl());

            // Check if already earned
            Optional<Achievement> earned = earnedAchievements.stream()
                    .filter(a -> a.getName().equals(definition.getName()))
                    .findFirst();

            if (earned.isPresent()) {
                progress.setIsCompleted(true);
                progress.setCurrentProgress(definition.getCriteriaValue());
                progress.setTarget(definition.getCriteriaValue());
                progress.setProgressPercentage(100.0);
                progress.setEarnedAt(earned.get().getEarnedAt());
            } else {
                // Calculate progress
                Integer currentProgress = calculateProgress(user, definition);
                progress.setCurrentProgress(currentProgress);
                progress.setTarget(definition.getCriteriaValue());
                progress.setIsCompleted(currentProgress >= definition.getCriteriaValue());
                progress.setProgressPercentage(
                        Math.min((currentProgress * 100.0) / definition.getCriteriaValue(), 100.0));
                progress.setEarnedAt(null);
            }

            progressList.add(progress);
        }

        return progressList;
    }

    public List<AchievementDefinition> getAllAchievementDefinitions() {
        return achievementDefinitionRepository.findByIsActiveTrue();
    }

    public List<AchievementResponse> getRecentAchievements(Long userId, int limit) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // This requires a custom method in AchievementRepository
        // For now, get all and limit manually
        List<Achievement> achievements = achievementRepository.findByUserOrderByEarnedAtDesc(user);

        return achievements.stream()
                .limit(limit)
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private Integer calculateProgress(User user, AchievementDefinition definition) {
        switch (definition.getCriteriaType().toUpperCase()) {
            case "STREAK":
                return user.getCurrentStreak();
            case "TASK_COUNT":
                return userTaskRepository.countByUser(user);
            case "POINTS":
                return user.getTotalPoints();
            case "BINGO_LINES":
                return bingoCardRepository.findByUserAndIsActiveTrue(user)
                        .map(BingoCard::getCompletedLines)
                        .orElse(0);
            case "LEVEL":
                return user.getLevel();
            case "DAILY_TASKS":
                LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
                LocalDateTime endOfDay = LocalDateTime.now().toLocalDate().atTime(23, 59, 59);
                return userTaskRepository.countByUserAndCompletedAtBetween(user, startOfDay, endOfDay);
            default:
                return 0;
        }
    }

    public void checkAndAwardAchievements(User user) {
        List<AchievementDefinition> definitions = achievementDefinitionRepository.findByIsActiveTrue();
        List<Achievement> userAchievements = achievementRepository.findByUser(user);

        for (AchievementDefinition definition : definitions) {
            // Skip if already earned
            boolean alreadyEarned = userAchievements.stream()
                    .anyMatch(a -> a.getName().equals(definition.getName()));

            if (alreadyEarned) continue;

            // Check criteria
            Integer progress = calculateProgress(user, definition);

            if (progress >= definition.getCriteriaValue()) {
                // Award achievement
                Achievement achievement = new Achievement();
                achievement.setUser(user);
                achievement.setName(definition.getName());
                achievement.setDescription(definition.getDescription());
                achievement.setIconUrl(definition.getIconUrl());
                achievement.setCategory(definition.getCategory());
                achievement.setPointsReward(definition.getPointsReward());
                achievement.setIsRare(definition.getIsRare());
                achievement.setEarnedAt(LocalDateTime.now());

                achievementRepository.save(achievement);

                // Add points to user
                user.setTotalPoints(user.getTotalPoints() + definition.getPointsReward());
                userRepository.save(user);

                // You could trigger a notification here
            }
        }
    }

    private AchievementResponse convertToResponse(Achievement achievement) {
        return new AchievementResponse(
                achievement.getId(),
                achievement.getName(),
                achievement.getDescription(),
                achievement.getIconUrl(),
                achievement.getEarnedAt()
        );
    }

    // In AchievementService.java
//    public List<AchievementResponse> getUserAchievements(Long userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
//
//        List<Achievement> achievements = achievementRepository.findByUserOrderByEarnedAtDesc(user);
//
//        return achievements.stream()
//                .map(this::convertToResponse)
//                .collect(Collectors.toList());
//    }
}