package com.bingotask.repository;

import com.bingotask.model.BingoCard;
import com.bingotask.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BingoCardRepository extends JpaRepository<BingoCard, Long> {

    Optional<BingoCard> findByUserAndIsActiveTrue(User user);

    List<BingoCard> findByUser(User user);

    List<BingoCard> findByIsActiveTrue();

    List<BingoCard> findByStartDateAfter(LocalDate date);

    @Query("SELECT bc FROM BingoCard bc " +
            "WHERE bc.user = :user " +
            "AND bc.startDate <= :date " +
            "AND bc.endDate >= :date")
    Optional<BingoCard> findByUserAndDate(@Param("user") User user,
                                          @Param("date") LocalDate date);

    @Query("SELECT COUNT(bc) FROM BingoCard bc " +
            "WHERE bc.completedLines >= :minLines")
    Long countByCompletedLinesGreaterThanEqual(@Param("minLines") Integer minLines);
}