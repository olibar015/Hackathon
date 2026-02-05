package com.bingotask.service;

import com.bingotask.dto.request.LoginRequest;
import com.bingotask.dto.request.RegisterRequest;
import com.bingotask.dto.response.AuthResponse;
import com.bingotask.dto.response.UserProfileResponse;
import com.bingotask.exception.BadRequestException;
import com.bingotask.model.User;
import com.bingotask.repository.UserRepository;
import com.bingotask.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserService userService;

    public AuthResponse register(RegisterRequest registerRequest) {
        // Check if username already exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new BadRequestException("Username is already taken!");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("Email is already in use!");
        }

        // Create new user
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setLevel(1);
        user.setXp(0);
        user.setXpToNextLevel(100);
        user.setTotalPoints(0);
        user.setCurrentStreak(0);
        user.setBestStreak(0);
        user.setLastActivityDate(LocalDate.now());
        user.setRole(User.Role.USER);

        User savedUser = userRepository.save(user);

        // Generate JWT token
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        registerRequest.getEmail(),
                        registerRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        // Get user profile
        UserProfileResponse userProfile = userService.getUserProfile(savedUser.getUsername());

        return new AuthResponse(jwt, "Bearer", userProfile);
    }

    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        // Get user by email
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BadRequestException("User not found"));

        // Update last activity date
        user.setLastActivityDate(LocalDate.now());
        userRepository.save(user);

        // Get user profile
        UserProfileResponse userProfile = userService.getUserProfile(user.getUsername());

        return new AuthResponse(jwt, "Bearer", userProfile);
    }

    public UserProfileResponse getCurrentUser(String username) {
        return userService.getUserProfile(username);
    }
}