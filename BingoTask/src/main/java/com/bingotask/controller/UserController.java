package com.bingotask.controller;

import com.bingotask.dto.request.UpdateProfileRequest;
import com.bingotask.dto.response.AchievementResponse;
import com.bingotask.dto.response.UserProfileResponse;
import com.bingotask.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile/{username}")
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserProfile(username));
    }

    @GetMapping("/{username}/achievements")
    public ResponseEntity<List<AchievementResponse>> getUserAchievements(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserAchievements(username));
    }

    @GetMapping("/{username}/streak")
    public ResponseEntity<Map<String, Integer>> getUserStreak(@PathVariable String username) {
        Map<String, Integer> streakInfo = new HashMap<>();
        streakInfo.put("current", userService.getCurrentStreak(username));
        streakInfo.put("best", userService.getBestStreak(username));
        return ResponseEntity.ok(streakInfo);
    }

    @PutMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequest request) {
        String username = userDetails.getUsername();
        return ResponseEntity.ok(userService.updateProfile(username, request));
    }
}
