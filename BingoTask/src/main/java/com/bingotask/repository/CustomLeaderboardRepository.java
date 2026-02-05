package com.bingotask.repository;

import com.bingotask.dto.response.LeaderboardEntry;
import com.bingotask.repository.mapper.LeaderboardEntryRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class CustomLeaderboardRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<LeaderboardEntry> getWeeklyLeaderboard(LocalDate weekStart) {
        String query = """
            SELECT 
                ROW_NUMBER() OVER (ORDER BY SUM(COALESCE(ut.points_earned, 0)) DESC) as rank,
                u.username,
                u.level,
                u.current_streak,
                u.total_points,
                COUNT(DISTINCT a.id) as achievements_count,
                u.avatar_url
            FROM users u
            LEFT JOIN user_tasks ut ON u.id = ut.user_id 
                AND ut.completed_at >= ? 
                AND ut.completed_at < DATE_ADD(?, INTERVAL 7 DAY)
            LEFT JOIN achievements a ON u.id = a.user_id
            GROUP BY u.id, u.username, u.level, u.current_streak, u.total_points, u.avatar_url
            ORDER BY SUM(COALESCE(ut.points_earned, 0)) DESC
            LIMIT 50
            """;

        return jdbcTemplate.query(query, new LeaderboardEntryRowMapper(), weekStart, weekStart);
    }

    public List<LeaderboardEntry> getGlobalLeaderboard(int limit) {
        String query = """
            SELECT 
                ROW_NUMBER() OVER (ORDER BY u.total_points DESC, u.level DESC, u.current_streak DESC) as rank,
                u.username,
                u.level,
                u.current_streak,
                u.total_points,
                COUNT(DISTINCT a.id) as achievements_count,
                u.avatar_url
            FROM users u
            LEFT JOIN achievements a ON u.id = a.user_id
            GROUP BY u.id, u.username, u.level, u.current_streak, u.total_points, u.avatar_url
            ORDER BY u.total_points DESC, u.level DESC, u.current_streak DESC
            LIMIT ?
            """;

        return jdbcTemplate.query(query, new LeaderboardEntryRowMapper(), limit);
    }

    public LeaderboardEntry getUserRank(String username) {
        String query = """
            WITH ranked_users AS (
                SELECT 
                    username,
                    total_points,
                    level,
                    current_streak,
                    avatar_url,
                    ROW_NUMBER() OVER (ORDER BY total_points DESC, level DESC, current_streak DESC) as rank
                FROM users
            )
            SELECT 
                ru.rank,
                ru.username,
                ru.level,
                ru.current_streak,
                ru.total_points,
                COALESCE(a.achievements_count, 0) as achievements_count,
                ru.avatar_url
            FROM ranked_users ru
            LEFT JOIN (
                SELECT user_id, COUNT(*) as achievements_count 
                FROM achievements 
                GROUP BY user_id
            ) a ON ru.username = ?
            WHERE ru.username = ?
            """;

        List<LeaderboardEntry> results = jdbcTemplate.query(query, new LeaderboardEntryRowMapper(), username, username);
        return results.isEmpty() ? null : results.get(0);
    }
}