package com.bingotask.dto.request;

import lombok.Data;

@Data
public class TaskFilter {
    private Boolean activeOnly = true;
    private String category;
    private Integer minPoints;
    private Integer maxPoints;
    private Long userId;
    private Boolean completedToday;
}