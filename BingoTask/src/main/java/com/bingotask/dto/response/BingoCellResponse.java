package com.bingotask.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BingoCellResponse {
    private Long id;
    private TaskResponse task;
    private Integer positionX;
    private Integer positionY;
    private Boolean isCompleted;
    private LocalDateTime completedAt;
    private Integer pointsEarned;
}