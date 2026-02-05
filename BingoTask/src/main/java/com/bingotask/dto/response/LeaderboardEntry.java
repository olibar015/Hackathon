package com.bingotask.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardEntry {
    private Integer rank;
    private String username;
    private Integer level;
    private Integer streak;
    private Integer totalPoints;
    private Integer achievementsCount;
    private String avatarUrl;
    private Boolean isCurrentUser = false;
}