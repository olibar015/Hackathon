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
import java.util.Optional;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String avatarUrl;

    private Integer level = 1;

    private Integer xp = 0;

    @Column(name = "xp_to_next_level")
    private Integer xpToNextLevel = 100;

    @Column(name = "total_points")
    private Integer totalPoints = 0;

    @Column(name = "current_streak")
    private Integer currentStreak = 0;

    @Column(name = "best_streak")
    private Integer bestStreak = 0;

    @Column(name = "last_activity_date")
    private LocalDate lastActivityDate;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserTask> completedTasks = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Achievement> achievements = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<BingoCard> bingoCards = new ArrayList<>();


  @CreationTimestamp
    private LocalDateTime createdAt;

    public enum Role {
        USER,
        APPROVER,   // Can approve tasks for others
        ADMIN
    }
  // Add helper method to get active bingo card
    public Optional<BingoCard> getActiveBingoCard() {
      return bingoCards.stream()
        .filter(BingoCard::getIsActive)
        .findFirst();
    }
}
