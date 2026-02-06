package com.bingotask.security;

import com.bingotask.model.User;
import com.bingotask.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  @Autowired
  private UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
    // Try to find by email first, then by username
    User user = userRepository.findByEmail(identifier)
      .or(() -> userRepository.findByUsername(identifier))
      .orElseThrow(() -> new UsernameNotFoundException(
        "User not found with identifier: " + identifier
      ));

    return org.springframework.security.core.userdetails.User
      .builder()
      .username(user.getUsername())
      .password(user.getPassword())
      .roles(user.getRole().name())
      .build();
  }
}
