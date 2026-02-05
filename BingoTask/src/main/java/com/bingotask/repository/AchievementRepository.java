package com.bingotask.repository;

import com.bingotask.model.Achievement;
import com.bingotask.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {

    List<Achievement> findByUser(User user);

    List<Achievement> findByUserOrderByEarnedAtDesc(User user);

    Boolean existsByUserAndName(User user, String name);

    @Query("SELECT a FROM Achievement a " +
            "WHERE a.user = :user " +
            "ORDER BY a.earnedAt DESC " +
            "LIMIT :limit")
    List<Achievement> findRecentByUser(@Param("user") User user,
                                       @Param("limit") int limit);

    @Query("SELECT COUNT(DISTINCT a.user) FROM Achievement a WHERE a.name = :achievementName")
    Long countUsersWithAchievement(@Param("achievementName") String achievementName);

    // Custom query for recent achievements
    List<Achievement> findTop5ByUserOrderByEarnedAtDesc(User user);

    // Count achievements by user
    Integer countByUser(User user);
}