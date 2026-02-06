package com.bingotask.dto.request;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class VerifyTaskRequest {
  @NotNull
  private Boolean approved;

  private String reviewNotes;
}
