package com.bingotask.controller;

import com.bingotask.dto.response.LeaderboardEntry; // Changed from model to dto.response
import com.bingotask.service.LeaderboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leaderboard")
@CrossOrigin(origins = "*")
public class LeaderboardController {

    @Autowired
    private LeaderboardService leaderboardService;

    @GetMapping("/global")
    public ResponseEntity<List<LeaderboardEntry>> getGlobalLeaderboard(
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(defaultValue = "0") Integer page) {
        return ResponseEntity.ok(leaderboardService.getGlobalLeaderboard(page, limit));
    }

    @GetMapping("/weekly")
    public ResponseEntity<List<LeaderboardEntry>> getWeeklyLeaderboard() {
        return ResponseEntity.ok(leaderboardService.getWeeklyLeaderboard());
    }

    @GetMapping("/friends")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<LeaderboardEntry>> getFriendsLeaderboard(
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        return ResponseEntity.ok(leaderboardService.getFriendsLeaderboard(username));
    }

    @GetMapping("/position/{username}")
    public ResponseEntity<Map<String, Object>> getUserRank(@PathVariable String username) {
        return ResponseEntity.ok(leaderboardService.getUserRank(username));
    }
}