package com.bingotask.service;

import com.bingotask.dto.response.BingoCardResponse;
import com.bingotask.dto.response.BingoCellResponse;
import com.bingotask.dto.response.TaskResponse;
import com.bingotask.model.*;
import com.bingotask.repository.BingoCardRepository;
import com.bingotask.repository.TaskRepository;
import com.bingotask.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Transactional
public class BingoService {

    @Autowired
    private BingoCardRepository bingoCardRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    private static final int GRID_SIZE = 5;
    private static final Random random = new Random();

//    public BingoCard generateBingoCard(Long userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        // Deactivate any existing active bingo card
//        bingoCardRepository.findByUserAndIsActiveTrue(user)
//                .ifPresent(card -> {
//                    card.setIsActive(false);
//                    bingoCardRepository.save(card);
//                });
//
//        // Create new bingo card
//        BingoCard bingoCard = new BingoCard();
//        bingoCard.setUser(user);
//        bingoCard.setIsActive(true);
//        bingoCard.setStartDate(LocalDate.now());
//        bingoCard.setEndDate(LocalDate.now().plusDays(7)); // Weekly bingo card
//        bingoCard.setCompletedLines(0);
//        bingoCard.setTotalPoints(0);
//
//        // Get active tasks
//        List<Task> activeTasks = taskRepository.findByIsActiveTrue();
//
//        // Shuffle and select 25 tasks (or GRID_SIZE * GRID_SIZE)
//        Collections.shuffle(activeTasks);
//        List<Task> selectedTasks = activeTasks.stream()
//                .limit(GRID_SIZE * GRID_SIZE)
//                .toList();
//
//        // Create cells for the bingo grid
//        int index = 0;
//        for (int x = 0; x < GRID_SIZE; x++) {
//            for (int y = 0; y < GRID_SIZE; y++) {
//                if (index < selectedTasks.size()) {
//                    Task task = selectedTasks.get(index);
//                    BingoCell cell = new BingoCell();
//                    cell.setBingoCard(bingoCard);
//                    cell.setTask(task);
//                    cell.setPositionX(x);
//                    cell.setPositionY(y);
//                    cell.setIsCompleted(false);
//                    cell.setPointsEarned(0);
//
//                    bingoCard.addCell(cell);
//                    index++;
//                }
//            }
//        }
//
//        return bingoCardRepository.save(bingoCard);
//    }

    public BingoCard generateBingoCard(Long userId) {
        // Get all active tasks
        List<Task> allTasks = taskRepository.findByIsActiveTrue();

        // Shuffle and pick 24 tasks (25 total cells - 1 free space)
        Collections.shuffle(allTasks);
        List<Task> selectedTasks = allTasks.stream()
                .limit(24)
                .collect(Collectors.toList());

        BingoCard card = new BingoCard();

        int index = 0;
        for (int y = 0; y < 5; y++) {
            for (int x = 0; x < 5; x++) {
                // Skip center (free space)
                if (x == 2 && y == 2) {
                    Task freeSpace = createFreeSpaceTask();
                    BingoCell freeCell = new BingoCell();
                    freeCell.setTask(freeSpace);
                    freeCell.setPositionX(x);
                    freeCell.setPositionY(y);
                    freeCell.setIsCompleted(true);  // Free space is always completed
                    freeCell.setCompletedAt(LocalDateTime.now());
                    card.addCell(freeCell);
                    continue;
                }

                if (index < selectedTasks.size()) {
                    Task task = selectedTasks.get(index);
                    BingoCell cell = new BingoCell();
                    cell.setTask(task);
                    cell.setPositionX(x);
                    cell.setPositionY(y);
                    cell.setIsCompleted(false);
                    card.addCell(cell);
                    index++;
                }
            }
        }

        return card;
    }

    private Task createFreeSpaceTask() {
        Task freeSpace = new Task();
        freeSpace.setTitle("FREE SPACE");
        freeSpace.setDescription("Automatic completion");
        freeSpace.setPoints(0);
        freeSpace.setCategory("free");
        freeSpace.setIconUrl("/icons/free.png");
        freeSpace.setIsActive(true);
        return taskRepository.save(freeSpace);
    }

    public BingoCard getCurrentBingoCard(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return bingoCardRepository.findByUserAndIsActiveTrue(user)
                .orElseGet(() -> generateBingoCard(userId));
    }

    public void updateBingoCard(User user, Task completedTask) {
        BingoCard bingoCard = bingoCardRepository.findByUserAndIsActiveTrue(user)
                .orElse(null);

        if (bingoCard == null) return;

        // Find and mark the corresponding cell
        bingoCard.getCells().stream()
                .filter(cell -> cell.getTask().getId().equals(completedTask.getId()))
                .filter(cell -> !Boolean.TRUE.equals(cell.getIsCompleted()))
                .findFirst()
                .ifPresent(cell -> {
                    cell.setIsCompleted(true);
                    cell.setCompletedAt(LocalDateTime.now());
                    cell.setPointsEarned(completedTask.getPoints());

                    // Update bingo card total points
                    bingoCard.setTotalPoints(bingoCard.getTotalPoints() + completedTask.getPoints());

                    // Check for completed lines
                    checkAndUpdateBingoLines(bingoCard);
                });

        bingoCardRepository.save(bingoCard);
    }

    private void checkAndUpdateBingoLines(BingoCard bingoCard) {
        List<List<Integer>> completedLines = new ArrayList<>();
        int completedLinesCount = 0;

        // Check horizontal lines
        for (int y = 0; y < GRID_SIZE; y++) {
            boolean lineComplete = true;
            for (int x = 0; x < GRID_SIZE; x++) {
                if (!bingoCard.isCellCompleted(x, y)) {
                    lineComplete = false;
                    break;
                }
            }
            if (lineComplete) {
                completedLinesCount++;
                List<Integer> line = new ArrayList<>();
                for (int x = 0; x < GRID_SIZE; x++) {
                    line.add(x);
                    line.add(y);
                }
                completedLines.add(line);
            }
        }

        // Check vertical lines
        for (int x = 0; x < GRID_SIZE; x++) {
            boolean lineComplete = true;
            for (int y = 0; y < GRID_SIZE; y++) {
                if (!bingoCard.isCellCompleted(x, y)) {
                    lineComplete = false;
                    break;
                }
            }
            if (lineComplete) {
                completedLinesCount++;
                List<Integer> line = new ArrayList<>();
                for (int y = 0; y < GRID_SIZE; y++) {
                    line.add(x);
                    line.add(y);
                }
                completedLines.add(line);
            }
        }

        // Check diagonal (top-left to bottom-right)
        boolean diagonal1Complete = true;
        for (int i = 0; i < GRID_SIZE; i++) {
            if (!bingoCard.isCellCompleted(i, i)) {
                diagonal1Complete = false;
                break;
            }
        }
        if (diagonal1Complete) {
            completedLinesCount++;
            List<Integer> line = new ArrayList<>();
            for (int i = 0; i < GRID_SIZE; i++) {
                line.add(i);
                line.add(i);
            }
            completedLines.add(line);
        }

        // Check diagonal (top-right to bottom-left)
        boolean diagonal2Complete = true;
        for (int i = 0; i < GRID_SIZE; i++) {
            if (!bingoCard.isCellCompleted(GRID_SIZE - 1 - i, i)) {
                diagonal2Complete = false;
                break;
            }
        }
        if (diagonal2Complete) {
            completedLinesCount++;
            List<Integer> line = new ArrayList<>();
            for (int i = 0; i < GRID_SIZE; i++) {
                line.add(GRID_SIZE - 1 - i);
                line.add(i);
            }
            completedLines.add(line);
        }

        // Update completed lines count
        bingoCard.setCompletedLines(completedLinesCount);
    }

    public List<List<Integer>> getCompletedLines(Long bingoCardId) {
        BingoCard bingoCard = bingoCardRepository.findById(bingoCardId)
                .orElseThrow(() -> new RuntimeException("Bingo card not found"));

        return calculateCompletedLines(bingoCard);
    }

    private List<List<Integer>> calculateCompletedLines(BingoCard bingoCard) {
        List<List<Integer>> completedLines = new ArrayList<>();

        // Add logic to calculate completed lines (similar to checkAndUpdateBingoLines)
        // Return list of coordinates for each completed line

        return completedLines;
    }

    // In BingoService.java
    public BingoCardResponse getBingoCardResponse(Long userId) {
        BingoCard bingoCard = getCurrentBingoCard(userId);

        BingoCardResponse response = new BingoCardResponse();
        response.setId(bingoCard.getId());
        response.setStartDate(bingoCard.getStartDate());
        response.setEndDate(bingoCard.getEndDate());
        response.setTotalPoints(bingoCard.getTotalPoints());
        response.setIsActive(bingoCard.getIsActive());

        // Calculate completed lines and their coordinates
        List<List<Integer>> completedLinesCoordinates = calculateCompletedLinesCoordinates(bingoCard);
        response.setCompletedLinesCoordinates(completedLinesCoordinates); // Set coordinates
        response.setCompletedLinesCount(completedLinesCoordinates.size()); // Set count

        // Calculate completed cells
        int completedCells = (int) bingoCard.getCells().stream()
                .filter(cell -> Boolean.TRUE.equals(cell.getIsCompleted()))
                .count();
        response.setCompletedCells(completedCells);

        // Convert cells to grid
        List<List<BingoCellResponse>> grid = convertCellsToGrid(bingoCard);
        response.setGrid(grid);

        return response;
    }

    private List<List<Integer>> calculateCompletedLinesCoordinates(BingoCard bingoCard) {
        List<List<Integer>> completedLines = new ArrayList<>();
        int gridSize = 5;

        // Check horizontal lines
        for (int y = 0; y < gridSize; y++) {
            boolean lineComplete = true;
            List<Integer> line = new ArrayList<>();
            for (int x = 0; x < gridSize; x++) {
                if (!bingoCard.isCellCompleted(x, y)) {
                    lineComplete = false;
                    break;
                }
                line.add(x);
                line.add(y);
            }
            if (lineComplete) {
                completedLines.add(line);
            }
        }

        // Check vertical lines
        for (int x = 0; x < gridSize; x++) {
            boolean lineComplete = true;
            List<Integer> line = new ArrayList<>();
            for (int y = 0; y < gridSize; y++) {
                if (!bingoCard.isCellCompleted(x, y)) {
                    lineComplete = false;
                    break;
                }
                line.add(x);
                line.add(y);
            }
            if (lineComplete) {
                completedLines.add(line);
            }
        }

        // Check diagonal (top-left to bottom-right)
        boolean diagonal1Complete = true;
        List<Integer> diagonal1 = new ArrayList<>();
        for (int i = 0; i < gridSize; i++) {
            if (!bingoCard.isCellCompleted(i, i)) {
                diagonal1Complete = false;
                break;
            }
            diagonal1.add(i);
            diagonal1.add(i);
        }
        if (diagonal1Complete) {
            completedLines.add(diagonal1);
        }

        // Check diagonal (top-right to bottom-left)
        boolean diagonal2Complete = true;
        List<Integer> diagonal2 = new ArrayList<>();
        for (int i = 0; i < gridSize; i++) {
            if (!bingoCard.isCellCompleted(gridSize - 1 - i, i)) {
                diagonal2Complete = false;
                break;
            }
            diagonal2.add(gridSize - 1 - i);
            diagonal2.add(i);
        }
        if (diagonal2Complete) {
            completedLines.add(diagonal2);
        }

        return completedLines;
    }

    private List<List<BingoCellResponse>> convertCellsToGrid(BingoCard bingoCard) {
        List<List<BingoCellResponse>> grid = new ArrayList<>();
        int gridSize = 5;

        for (int y = 0; y < gridSize; y++) {
            List<BingoCellResponse> row = new ArrayList<>();
            for (int x = 0; x < gridSize; x++) {
                BingoCell cell = bingoCard.getCellAtPosition(x, y);
                row.add(convertToCellResponse(cell));
            }
            grid.add(row);
        }

        return grid;
    }

    private BingoCellResponse convertToCellResponse(BingoCell cell) {
        if (cell == null) {
            return null;
        }

        BingoCellResponse response = new BingoCellResponse();
        response.setId(cell.getId());
        response.setPositionX(cell.getPositionX());
        response.setPositionY(cell.getPositionY());
        response.setIsCompleted(cell.getIsCompleted());
        response.setCompletedAt(cell.getCompletedAt());
        response.setPointsEarned(cell.getPointsEarned());

        if (cell.getTask() != null) {
            Task task = cell.getTask();
            TaskResponse taskResponse = new TaskResponse();
            taskResponse.setId(task.getId());
            taskResponse.setTitle(task.getTitle());
            taskResponse.setDescription(task.getDescription());
            taskResponse.setPoints(task.getPoints());
            taskResponse.setCategory(task.getCategory());
            taskResponse.setIconUrl(task.getIconUrl());
            response.setTask(taskResponse);
        }

        return response;
    }


}