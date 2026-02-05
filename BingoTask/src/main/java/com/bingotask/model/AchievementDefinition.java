package com.bingotask.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "achievement_definitions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AchievementDefinition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String description;

    private String iconUrl;

    @Enumerated(EnumType.STRING)
    private AchievementCategory category;

    @Column(nullable = false)
    private String criteriaType; // "STREAK", "TASK_COUNT", "POINTS", "BINGO_LINES", etc.

    @Column(nullable = false)
    private Integer criteriaValue; // e.g., 7 for 7-day streak

    @Column(nullable = false)
    private Integer pointsReward;

    private Boolean isActive;

    private Boolean isRare;

    @Column(columnDefinition = "TEXT")
    private String tips; // Tips on how to earn this achievement
}