package com.vuntfx17196.service.impl;

import com.vuntfx17196.dto.UserDTO;
import com.vuntfx17196.dto.UserRegisterDTO;
import com.vuntfx17196.dto.UserUpdateDTO;
import com.vuntfx17196.model.Role;
import com.vuntfx17196.model.User;
import com.vuntfx17196.repository.RoleRepository;
import com.vuntfx17196.repository.UserRepository;
import com.vuntfx17196.security.RandomUtil;
import com.vuntfx17196.service.UserService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

  UserRepository userRepository;
  RoleRepository roleRepository;
  PasswordEncoder passwordEncoder;

  public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
      PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.passwordEncoder = passwordEncoder;
  }

/*    public Optional<User> activateRegistration(String key) {
        return userRepository.findOneByActivationKey(key).map(user -> {
            // activate given user for the registration key.
            user.setActivated(true);
            user.setActivationKey(null);
            return user;
        });
    }*/

  public Optional<User> completePasswordReset(String newPassword, String key) {
    // Key expired 1 day.
    return userRepository.findOneByResetKey(key)
        .filter(user -> user.getResetDate().isAfter(Instant.now().minus(1, ChronoUnit.DAYS)))
        .map(user -> {
          user.setPassword(passwordEncoder.encode(newPassword));
          user.setResetKey(null);
          user.setResetDate(null);
          user.setLastModifiedDate(Instant.now());
          return user;
        });
  }

  public Optional<User> requestPasswordReset(String mail) {
    return userRepository.findOneByEmailIgnoreCase(mail).filter(User::isActivated).map(user -> {
      user.setResetKey(RandomUtil.generateResetKey());
      user.setResetDate(Instant.now());
      return user;
    });
  }

  @Transactional(readOnly = true)
  @Override
  public Page<User> getAllUsers(int pageNum, int pageSize, String sortField, String sortDir) {
    Pageable pageable = PageRequest.of(pageNum - 1, pageSize,
        sortDir.equals("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending());
    return userRepository.findAll(pageable);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<User> getAllUsersByRole(Role role, int pageNum, int pageSize, String sortField,
      String sortDir) {
    Pageable pageable = PageRequest.of(pageNum - 1, pageSize,
        sortDir.equals("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending());
    return userRepository.findAllByRoles(role, pageable);
  }

  @Override
  public Page<User> searchAllByEmail(String keyword, int pageNum, int pageSize,
      String sortField,
      String sortDir) {
    Pageable pageable = PageRequest.of(pageNum - 1, pageSize,
        sortDir.equals("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending());
    return userRepository.searchAllByEmailContainingIgnoreCaseOrNameContainingIgnoreCase(keyword,
        keyword, pageable);
  }

  @Override
  public Page<User> searchAllByEmailAndRole(String keyword, Role role,
      int pageNum, int pageSize,
      String sortField, String sortDir) {
    Pageable pageable = PageRequest.of(pageNum - 1, pageSize,
        sortDir.equals("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending());
    return userRepository.searchAllByEmailContainingIgnoreCaseAndRolesOrNameContainingIgnoreCaseAndRoles(
        keyword, role, keyword, role, pageable);
  }

  // nguoi dung dang ky moi
  @Override
  public User registerUser(UserRegisterDTO userDto, String password) {
    User newUser = new User();
    newUser.setName(userDto.getName());
    newUser.setEmail(userDto.getEmail());
    newUser.setPassword(passwordEncoder.encode(password));
    newUser.setActivated(true);
    List<Role> roles = new ArrayList<>();
    roles.add(roleRepository.findByName("ROLE_USER"));
    newUser.setRoles(roles);

    userRepository.save(newUser);
    return newUser;
  }

  public void createUser(UserDTO userDTO) {
    User newUser = new User();
    newUser.setName(userDTO.getName());
    newUser.setEmail(userDTO.getEmail());
    newUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
    newUser.setActivated(true);
    if (userDTO.getPoints() != null && userDTO.getPoints() > 0) {
      newUser.setPoints(userDTO.getPoints());
    }
    if (userDTO.getRoleIds() != null) {
      List<Role> roles = userDTO.getRoleIds().stream().map(roleRepository::findById)
          .filter(Optional::isPresent).map(Optional::get).toList();
      newUser.setRoles(roles);
    }

    userRepository.save(newUser);
  }

  @Override
  public void removeUserById(User user) {
//        user.setActivated(false);
//        userRepository.save(user);
    userRepository.delete(user);
  }

  @Override
  @Transactional(readOnly = true)
  public User getUserByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  @Override
  public Optional<User> getUserWithRolesByEmail(String email) {
    return userRepository.findOneWithRolesByEmailIgnoreCase(email);
  }

  @Override
  public Optional<User> getUserById(int id) {
    return userRepository.findById(id);
  }

  @Override
  public long countByRole(String role) {
    Role rolee = roleRepository.findByName(role);
    return userRepository.countByRoles(rolee);
  }

  @Override
  public String resetPasswordByAdmin(User user) {
    String randomPassword = "acbd1234";
    String encryptedPassword = passwordEncoder.encode(randomPassword);
    user.setPassword(encryptedPassword);
    userRepository.save(user);

    return randomPassword;
  }

  @Override
  public void updateUser(UserUpdateDTO userDTO) {
    Optional.of(userRepository.findById(userDTO.getId())).filter(Optional::isPresent)
        .map(Optional::get).map(user -> {
          user.setName(userDTO.getName());
          user.setPoints(userDTO.getPoints());
          user.setActivated(userDTO.isActivated());
          user.setLastModifiedDate(userDTO.getLastModifiedDate());
          List<Role> managedRoles = user.getRoles();
          managedRoles.clear();
          userDTO.getRoleIds().stream().map(roleRepository::findById).filter(Optional::isPresent)
              .map(Optional::get).forEach(managedRoles::add);
          return user;
        });
  }

  @Override
  public void updateInfoUser(User user, String name, String password) {
    if (!name.isBlank()) { // Change name
      user.setName(name);
    } else { // Change password
      user.setPassword(passwordEncoder.encode(password));
    }
    user.setLastModifiedDate(Instant.now());
    userRepository.save(user);
  }

}
