package com.bingotask.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "bingo_card_tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BingoCardTask {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "bingo_card_id", nullable = false)
  private BingoCard bingoCard;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "task_id", nullable = false)
  private Task task;

  @Column(name = "position_x")
  private Integer positionX;

  @Column(name = "position_y")
  private Integer positionY;

  @Column(name = "is_completed")
  private Boolean isCompleted = false;

  @Column(name = "completed_date")
  private LocalDateTime completedDate;

  @Column(name = "is_verified")
  private Boolean isVerified = false;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "verified_by")
  private User verifiedBy;

  @Column(name = "verified_date")
  private LocalDateTime verifiedDate;

  // Helper methods
  public void complete() {
    this.isCompleted = true;
    this.completedDate = LocalDateTime.now();
  }

  public void verify(User verifier) {
    this.isVerified = true;
    this.verifiedBy = verifier;
    this.verifiedDate = LocalDateTime.now();
  }
}
