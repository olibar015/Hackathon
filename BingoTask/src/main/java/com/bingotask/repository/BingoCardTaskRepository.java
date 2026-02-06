package com.bingotask.repository;

import com.bingotask.model.BingoCard;
import com.bingotask.model.BingoCardTask;
import com.bingotask.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BingoCardTaskRepository extends JpaRepository<BingoCardTask, Long> {

  List<BingoCardTask> findByBingoCardId(Long bingoCardId);

  Optional<BingoCardTask> findByBingoCardIdAndTaskId(Long bingoCardId, Long taskId);

  @Query("SELECT bct FROM BingoCardTask bct WHERE bct.bingoCard = :bingoCard AND bct.positionX = :x AND bct.positionY = :y")
  Optional<BingoCardTask> findByPosition(@Param("bingoCard") BingoCard bingoCard,
                                         @Param("x") Integer x,
                                         @Param("y") Integer y);

  @Query("SELECT COUNT(bct) FROM BingoCardTask bct WHERE bct.bingoCard = :bingoCard AND bct.isCompleted = true")
  int countCompletedTasksByBingoCard(@Param("bingoCard") BingoCard bingoCard);

  @Query("SELECT bct FROM BingoCardTask bct WHERE bct.bingoCard.isCompleted = false AND bct.isVerified = false")
  List<BingoCardTask> findPendingVerifications();

  // Add this method
  @Query("SELECT COUNT(bct) FROM BingoCardTask bct WHERE bct.bingoCard.user = :user AND bct.isCompleted = true")
  int countByBingoCardUserAndIsCompletedTrue(@Param("user") User user);
}
