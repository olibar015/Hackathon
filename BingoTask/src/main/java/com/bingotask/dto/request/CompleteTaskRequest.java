package com.bingotask.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class CompleteTaskRequest {
    @NotNull
    private Long taskId;
}