package com.bingotask.model;

public enum AchievementCategory {
    STREAK("Streak"),
    COMPLETION("Task Completion"),
    BINGO("Bingo"),
    POINTS("Points"),
    LEVEL("Level"),
    SOCIAL("Social"),
    CHALLENGE("Special Challenges"),
    DAILY("Daily Goals"),
    WEEKLY("Weekly Goals"),
    MONTHLY("Monthly Goals");

    private final String displayName;

    AchievementCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}