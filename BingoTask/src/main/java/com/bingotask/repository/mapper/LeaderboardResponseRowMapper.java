package com.bingotask.repository.mapper;

import com.bingotask.dto.response.LeaderboardResponse;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LeaderboardResponseRowMapper implements RowMapper<LeaderboardResponse> {

    @Override
    public LeaderboardResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
        LeaderboardResponse response = new LeaderboardResponse();

        // This is more complex as LeaderboardResponse contains multiple entries
        // You might need to use a different approach
        return response;
    }
}