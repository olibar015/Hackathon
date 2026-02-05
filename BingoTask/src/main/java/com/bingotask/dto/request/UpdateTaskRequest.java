package com.bingotask.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class UpdateTaskRequest {
    @NotBlank
    private String title;

    private String description;

    @NotNull
    private Integer points;

    private String category;

    private Integer positionX;
    private Integer positionY;

    @NotNull
    private Boolean isActive;
}