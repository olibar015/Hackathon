package com.bingotask.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "bingoCard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BingoCell> cells = new ArrayList<>();

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "completed_lines")
    private Integer completedLines = 0;

    @Column(name = "total_points")
    private Integer totalPoints = 0;

    // Helper method to add a cell
    public void addCell(BingoCell cell) {
        cells.add(cell);
        cell.setBingoCard(this);
    }

    // Helper method to remove a cell
    public void removeCell(BingoCell cell) {
        cells.remove(cell);
        cell.setBingoCard(null);
    }

    // Method to get cell at position
    public BingoCell getCellAtPosition(int x, int y) {
        return cells.stream()
                .filter(cell -> cell.getPositionX() == x && cell.getPositionY() == y)
                .findFirst()
                .orElse(null);
    }

    // Method to check if cell at position is completed
    public boolean isCellCompleted(int x, int y) {
        BingoCell cell = getCellAtPosition(x, y);
        return cell != null && Boolean.TRUE.equals(cell.getIsCompleted());
    }

    // Method to mark cell as completed
    public void markCellAsCompleted(int x, int y, Integer pointsEarned) {
        BingoCell cell = getCellAtPosition(x, y);
        if (cell != null) {
            cell.setIsCompleted(true);
            cell.setCompletedAt(LocalDateTime.now());
            cell.setPointsEarned(pointsEarned);
            this.totalPoints += pointsEarned;
        }
    }
}