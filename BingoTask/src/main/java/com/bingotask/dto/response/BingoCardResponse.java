package com.bingotask.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BingoCardResponse {
    private Long id;
    private List<List<BingoCellResponse>> grid;
    private Integer completedCells;
    private Integer totalPoints;
    private Integer completedLinesCount; // Number of completed lines
    private List<List<Integer>> completedLinesCoordinates; // Coordinates of completed lines
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;
}