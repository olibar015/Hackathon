package com.bingotask.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "points_earned")
    private Integer pointsEarned;

    @Column(name = "counted_for_bingo")
    private Boolean countedForBingo = false;
}