package com.vuntfx17196.service;

import com.vuntfx17196.model.CustomUserDetail;
import com.vuntfx17196.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Authenticate a user from the database.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  public CustomUserDetailsService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String email) {
    return userRepository.findOneWithRolesByEmailIgnoreCase(email).map(CustomUserDetail::new)
        .orElseThrow(() -> new UsernameNotFoundException(
            "User with email " + email + " was not found in the database"));
  }
}
