package com.bingotask.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "task_assignments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskAssignment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "assigner_id", nullable = false)
  private User assigner;

  @ManyToOne
  @JoinColumn(name = "assignee_id", nullable = false)
  private User assignee;

  @ManyToOne
  @JoinColumn(name = "task_id", nullable = false)
  private Task task;

  @Column(name = "assigned_at")
  private LocalDateTime assignedAt;

  @Column(name = "due_date")
  private LocalDate dueDate;

  @Enumerated(EnumType.STRING)
  private AssignmentStatus status = AssignmentStatus.PENDING;

  @Column(name = "completed_at")
  private LocalDateTime completedAt;

  @Column(name = "verified_at")
  private LocalDateTime verifiedAt;

  @ManyToOne
  @JoinColumn(name = "verifier_id")
  private User verifier;

  private String notes;

  @Column(name = "proof_url")
  private String proofUrl;

  public enum AssignmentStatus {
    PENDING,      // Task assigned, not yet completed
    COMPLETED,    // User marked as completed, needs verification
    VERIFIED,     // Approver verified completion
    REJECTED      // Approver rejected the completion
  }

  @PrePersist
  protected void onCreate() {
    if (assignedAt == null) {
      assignedAt = LocalDateTime.now();
    }
  }
}
