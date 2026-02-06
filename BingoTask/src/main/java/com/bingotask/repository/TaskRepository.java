package com.bingotask.repository;

import com.bingotask.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    List<Task> findByIsActiveTrue();

    List<Task> findByCategory(String category);

    List<Task> findByCategoryAndIsActiveTrue(String category);

    List<Task> findByPositionXAndPositionY(Integer positionX, Integer positionY);

    @Query("SELECT t FROM Task t WHERE t.positionX IS NOT NULL AND t.positionY IS NOT NULL")
    List<Task> findAllWithPosition();

    @Query("SELECT t FROM Task t WHERE t.points BETWEEN :minPoints AND :maxPoints")
    List<Task> findByPointsRange(@Param("minPoints") Integer minPoints,
                                 @Param("maxPoints") Integer maxPoints);

    @Query("SELECT t.category, COUNT(t) FROM Task t GROUP BY t.category")
    List<Object[]> countTasksByCategory();

    @Query("SELECT COUNT(t) FROM Task t WHERE t.isActive = true")
    Long countActiveTasks();

    long countByIsActiveTrue();

    @Query("SELECT t FROM Task t WHERE t.positionX = :x AND t.positionY = :y")
    Optional<Task> findByPosition(@Param("x") Integer x, @Param("y") Integer y);

    List<Task> findByPositionXNotNullAndPositionYNotNull();

    @Query("SELECT t FROM Task t WHERE t.positionX IS NULL AND t.positionY IS NULL AND t.isActive = true")
    List<Task> findTasksWithoutPosition();
    // In TaskRepository.java
    Optional<Task> findByIdAndIsActiveTrue(Long id);
}
