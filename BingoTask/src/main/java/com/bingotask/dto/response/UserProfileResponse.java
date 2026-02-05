package com.bingotask.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private Long id;
    private String username;
    private String email;
    private String avatarUrl;
    private Integer level;
    private Integer xp;
    private Integer xpToNextLevel;
    private Integer totalPoints;
    private Integer currentStreak;
    private Integer bestStreak;
    private Integer completedTasks;
    private Integer bingoLines;
    private List<AchievementResponse> recentAchievements;
    private LocalDateTime createdAt;
}