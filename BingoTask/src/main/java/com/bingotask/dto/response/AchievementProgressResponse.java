package com.bingotask.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AchievementProgressResponse {
    private Long achievementId;
    private String name;
    private String description;
    private String iconUrl;
    private Integer currentProgress;
    private Integer target;
    private Boolean isCompleted; // Lombok will generate getIsCompleted() and setIsCompleted()
    private Double progressPercentage;
    private LocalDateTime earnedAt;
}