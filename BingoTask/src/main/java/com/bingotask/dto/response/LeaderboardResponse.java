package com.bingotask.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardResponse {
    private List<LeaderboardEntry> entries;
    private Integer userRank;
    private Integer userTotalPoints;
    private Long totalUsers;
}