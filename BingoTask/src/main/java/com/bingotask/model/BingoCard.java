package com.bingotask.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bingo_cards")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BingoCard {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "card_name")
  private String cardName = "Bingo Card";

  @Column(name = "start_date")
  private LocalDate startDate;  // Add this field

  @Column(name = "end_date")
  private LocalDate endDate;    // Add this field

  @Column(name = "is_active")
  private Boolean isActive = true;

  @Column(name = "is_completed")
  private Boolean isCompleted = false;

  @Column(name = "completed_lines")
  private Integer completedLines = 0;

  @Column(name = "completed_date")
  private LocalDateTime completedDate;

  @Column(name = "is_approved")
  private Boolean isApproved = false;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "approved_by")
  private User approvedBy;

  @Column(name = "approved_date")
  private LocalDateTime approvedDate;

  @OneToMany(mappedBy = "bingoCard", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<BingoCardTask> tasks = new ArrayList<>();

  @CreationTimestamp
  @Column(name = "created_at")
  private LocalDateTime createdAt;

  // Add constructor or pre-persist method to set dates
  @PrePersist
  protected void onCreate() {
    if (startDate == null) {
      startDate = LocalDate.now();
    }
    if (endDate == null) {
      endDate = startDate.plusDays(7); // 1 week duration by default
    }
  }

  // Helper methods
  public boolean isReadyForApproval() {
    return isCompleted && !isApproved;
  }

  public void complete() {
    this.isCompleted = true;
    this.completedDate = LocalDateTime.now();
  }

  public void approve(User approver) {
    this.isApproved = true;
    this.approvedBy = approver;
    this.approvedDate = LocalDateTime.now();
  }

  public void updateCompletedLines(int lines) {
    this.completedLines = lines;
  }
}
