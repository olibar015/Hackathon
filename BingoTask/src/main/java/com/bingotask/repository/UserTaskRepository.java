package com.bingotask.repository;

import com.bingotask.model.Task;
import com.bingotask.model.User;
import com.bingotask.model.UserTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserTaskRepository extends JpaRepository<UserTask, Long> {

    // Add this method for pagination
    Page<UserTask> findByUser(User user, Pageable pageable);
    List<UserTask> findByTask(Task task);

    List<UserTask> findByUserAndCompletedAtBetween(User user,
                                                   LocalDateTime start,
                                                   LocalDateTime end);

    List<UserTask> findByUserAndTask(User user, Task task);

    Boolean existsByUserAndTaskAndCompletedAtBetween(User user,
                                                     Task task,
                                                     LocalDateTime start,
                                                     LocalDateTime end);

    Integer countByUser(User user);

    // Add this missing method
    @Query("SELECT COUNT(ut) FROM UserTask ut WHERE ut.user = :user AND ut.completedAt BETWEEN :start AND :end")
    Integer countByUserAndCompletedAtBetween(@Param("user") User user,
                                             @Param("start") LocalDateTime start,
                                             @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(SUM(ut.pointsEarned), 0) FROM UserTask ut " +
            "WHERE ut.user = :user " +
            "AND ut.completedAt >= :startDate " +
            "AND ut.completedAt <= :endDate")
    Integer sumPointsByUserAndDateRange(@Param("user") User user,
                                        @Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);
}