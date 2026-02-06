package com.bingotask.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignTaskRequest {
  private Long userId;
  private Long taskId;
  private Integer positionX;  // Optional: position on bingo card
  private Integer positionY;  // Optional: position on bingo card
}
