package com.bingotask.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BingoCardResponse {
  private Long id;
  private Long userId;
  private String cardName;
  private Boolean active;
  private Boolean completed;
  private Boolean approved;
  private LocalDateTime completedDate;
  private String approvedBy;
  private LocalDateTime approvedDate;
  private LocalDateTime createdAt;
  private List<BingoCardTaskResponse> tasks;
}
