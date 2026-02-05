package com.bingotask.repository.mapper;

import com.bingotask.dto.response.LeaderboardEntry;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LeaderboardEntryRowMapper implements RowMapper<LeaderboardEntry> {

    @Override
    public LeaderboardEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
        LeaderboardEntry entry = new LeaderboardEntry();
        entry.setRank(rs.getInt("rank"));
        entry.setUsername(rs.getString("username"));
        entry.setLevel(rs.getInt("level"));
        entry.setStreak(rs.getInt("current_streak"));
        entry.setTotalPoints(rs.getInt("total_points"));
        entry.setAchievementsCount(rs.getInt("achievements_count"));
        entry.setAvatarUrl(rs.getString("avatar_url"));
        return entry;
    }
}