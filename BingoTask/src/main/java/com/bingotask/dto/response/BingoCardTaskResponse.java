package com.bingotask.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BingoCardTaskResponse {
  private Long id;
  private Long taskId;
  private String taskTitle;
  private String taskDescription;
  private Integer taskPoints;
  private String taskCategory;
  private Integer positionX;
  private Integer positionY;
  private Boolean completed;
  private Boolean verified;
  private LocalDateTime completedDate;
  private String verifiedBy;
  private LocalDateTime verifiedDate;
}
