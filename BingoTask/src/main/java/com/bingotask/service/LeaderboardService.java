package com.bingotask.service;

import com.bingotask.dto.response.LeaderboardEntry;
import com.bingotask.exception.ResourceNotFoundException;
import com.bingotask.model.User;
import com.bingotask.model.UserTask;
import com.bingotask.repository.UserRepository;
import com.bingotask.repository.UserTaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class LeaderboardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserTaskRepository userTaskRepository;

    public List<LeaderboardEntry> getGlobalLeaderboard(int page, int limit) {
        Pageable pageable = PageRequest.of(page, limit,
                Sort.by(Sort.Direction.DESC, "totalPoints")
                        .and(Sort.by(Sort.Direction.DESC, "level"))
                        .and(Sort.by(Sort.Direction.DESC, "currentStreak")));

        Page<User> userPage = userRepository.findAll(pageable);

        List<LeaderboardEntry> leaderboard = new ArrayList<>();
        int rank = page * limit + 1; // Calculate starting rank

        for (User user : userPage.getContent()) {
            LeaderboardEntry entry = new LeaderboardEntry();
            entry.setRank(rank++);
            entry.setUsername(user.getUsername());
            entry.setLevel(user.getLevel());
            entry.setStreak(user.getCurrentStreak());
            entry.setTotalPoints(user.getTotalPoints());
            entry.setAvatarUrl(user.getAvatarUrl());
            // You'll need to calculate achievements count
            entry.setAchievementsCount(0); // Placeholder
            leaderboard.add(entry);
        }

        return leaderboard;
    }

    public List<LeaderboardEntry> getWeeklyLeaderboard() {
        LocalDate weekStart = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
        LocalDate weekEnd = weekStart.plusDays(6);

        // Get all users
        List<User> allUsers = userRepository.findAll();

        // Create leaderboard based on points earned this week
        List<LeaderboardEntry> leaderboard = new ArrayList<>();
        int rank = 1;

        for (User user : allUsers) {
            // Calculate weekly points (you need to implement this)
            int weeklyPoints = calculateWeeklyPoints(user, weekStart, weekEnd);

            if (weeklyPoints > 0) { // Only include users with activity
                LeaderboardEntry entry = new LeaderboardEntry();
                entry.setRank(rank++);
                entry.setUsername(user.getUsername());
                entry.setLevel(user.getLevel());
                entry.setStreak(user.getCurrentStreak());
                entry.setTotalPoints(weeklyPoints);
                entry.setAvatarUrl(user.getAvatarUrl());
                entry.setAchievementsCount(0); // Placeholder
                leaderboard.add(entry);
            }
        }

        // Sort by weekly points descending
        leaderboard.sort((a, b) -> b.getTotalPoints() - a.getTotalPoints());

        // Update ranks after sorting
        for (int i = 0; i < leaderboard.size(); i++) {
            leaderboard.get(i).setRank(i + 1);
        }

        return leaderboard;
    }

    public List<LeaderboardEntry> getFriendsLeaderboard(String username) {
        // For now, return global leaderboard
        // In a real app, you would have a friends system
        return getGlobalLeaderboard(0, 10);
    }

    public Map<String, Object> getUserRank(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        // Get all users sorted by total points
        List<User> allUsers = userRepository.findAll(Sort.by(Sort.Direction.DESC, "totalPoints"));

        // Find user's rank
        int rank = -1;
        for (int i = 0; i < allUsers.size(); i++) {
            if (allUsers.get(i).getId().equals(user.getId())) {
                rank = i + 1;
                break;
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("rank", rank);
        result.put("totalUsers", allUsers.size());
        result.put("level", user.getLevel());
        result.put("totalPoints", user.getTotalPoints());
        result.put("streak", user.getCurrentStreak());

        return result;
    }

    private int calculateWeeklyPoints(User user, LocalDate weekStart, LocalDate weekEnd) {
        // Uncomment this when you add the method to UserTaskRepository
        // Integer weeklyPoints = userTaskRepository.sumPointsByUserAndDateRange(
        //     user, weekStart.atStartOfDay(), weekEnd.atTime(23, 59, 59));
        // return weeklyPoints != null ? weeklyPoints : 0;

        // For now, return user's total points as placeholder
        return user.getTotalPoints();
    }

    // Keep your existing methods
    public Page<User> getLeaderboardPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "totalPoints")
                        .and(Sort.by(Sort.Direction.DESC, "level"))
                        .and(Sort.by(Sort.Direction.DESC, "currentStreak")));

        return userRepository.findAll(pageable);
    }

    public Page<UserTask> getUserActivity(Long userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Pageable pageable = PageRequest.of(page, size,
                Sort.by(Sort.Direction.DESC, "completedAt"));

        return userTaskRepository.findByUser(user, pageable);
    }
}