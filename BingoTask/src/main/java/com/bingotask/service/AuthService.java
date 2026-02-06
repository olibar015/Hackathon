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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    // Default to USER role if not specified
    User.Role userRole = User.Role.USER;

    // Check if role was provided
    if (registerRequest.getRole() != null && !registerRequest.getRole().trim().isEmpty()) {
      try {
        String roleInput = registerRequest.getRole().toUpperCase().trim();
        userRole = User.Role.valueOf(roleInput);

        // Check if trying to register with ADMIN or APPROVER role
        if (userRole == User.Role.ADMIN) {
          // Count how many users exist
          long userCount = userRepository.count();

          // Allow ADMIN/APPROVER registration ONLY if there are no users in the system
          // This is for initial setup only
          if (userCount > 0) {
            throw new BadRequestException(
              "ADMIN roles can only be assigned during initial setup. " +
                "Please register as USER and contact an administrator for role changes."
            );
          }
        }

      } catch (IllegalArgumentException e) {
        throw new BadRequestException(
          "Invalid role: " + registerRequest.getRole() +
            ". Valid roles are: USER, APPROVER, ADMIN"
        );
      }
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
    user.setRole(userRole);

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

//  // In CustomUserDetailsService, add this method:
//  public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
//    User user = userRepository.findByEmail(email)
//      .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
//
//    return new org.springframework.security.core.userdetails.User(
//      user.getUsername(),
//      user.getPassword(),
//      getAuthorities(user)
//    );
//  }

}
