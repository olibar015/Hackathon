package com.bingotask.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AchievementBadgeResponse {
    private String name;
    private String iconUrl;
    private String color; // for UI styling
    private Integer earnedCount;
    private Boolean isEarned;
    private LocalDateTime firstEarnedAt;
    private LocalDateTime lastEarnedAt;
}