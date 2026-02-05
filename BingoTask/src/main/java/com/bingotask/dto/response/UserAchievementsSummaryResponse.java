package com.bingotask.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAchievementsSummaryResponse {
    private Integer totalAchievements;
    private Integer earnedAchievements;
    private Integer rareAchievements; // achievements earned by < 10% of users
    private Map<String, Integer> achievementsByCategory; // category -> count
    private List<AchievementResponse> recentlyEarned;
    private List<AchievementProgressResponse> inProgress;
}