package com.bingotask.controller;

import com.bingotask.dto.response.AchievementProgressResponse;
import com.bingotask.dto.response.AchievementResponse;
import com.bingotask.model.AchievementDefinition;
import com.bingotask.service.AchievementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/achievements")
@CrossOrigin(origins = "*")
public class AchievementController {

    @Autowired
    private AchievementService achievementService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AchievementResponse>> getUserAchievements(@PathVariable Long userId) {
        return ResponseEntity.ok(achievementService.getUserAchievements(userId));
    }

    @GetMapping("/user/{userId}/progress")
    public ResponseEntity<List<AchievementProgressResponse>> getAchievementProgress(
            @PathVariable Long userId) {
        return ResponseEntity.ok(achievementService.getAchievementProgress(userId));
    }

    @GetMapping("/definitions")
    public ResponseEntity<List<AchievementDefinition>> getAllAchievementDefinitions() {
        return ResponseEntity.ok(achievementService.getAllAchievementDefinitions());
    }
}