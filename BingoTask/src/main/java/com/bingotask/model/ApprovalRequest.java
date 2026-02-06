package com.bingotask.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "approval_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalRequest {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_task_id", nullable = false)
  private UserTask userTask;

  @ManyToOne
  @JoinColumn(name = "requester_id", nullable = false)
  private User requester;

  @ManyToOne
  @JoinColumn(name = "approver_id")
  private User approver;

  @Column(name = "requested_at")
  private LocalDateTime requestedAt;

  @Enumerated(EnumType.STRING)
  private ApprovalStatus status = ApprovalStatus.PENDING;

  @Column(name = "reviewed_at")
  private LocalDateTime reviewedAt;

  @Column(name = "review_notes")
  private String reviewNotes;

  @Column(name = "proof_url")
  private String proofUrl;

  public enum ApprovalStatus {
    PENDING, APPROVED, REJECTED
  }

  @PrePersist
  protected void onCreate() {
    if (requestedAt == null) {
      requestedAt = LocalDateTime.now();
    }
  }
}
