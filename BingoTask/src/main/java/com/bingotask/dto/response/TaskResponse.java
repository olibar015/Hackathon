package com.bingotask.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private Integer points;
    private String category;
    private String iconUrl;
    private Integer positionX;
    private Integer positionY;
    private Boolean isCompleted;
    private LocalDateTime completedAt;
}