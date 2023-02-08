package com.vuntfx17196.service;

import com.vuntfx17196.dto.UserDTO;
import com.vuntfx17196.dto.UserRegisterDTO;
import com.vuntfx17196.dto.UserUpdateDTO;
import com.vuntfx17196.model.Role;
import com.vuntfx17196.model.User;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface UserService {

  Page<User> getAllUsers(int pageNum, int pageSize, String sortField, String sortDir);

  Page<User> getAllUsersByRole(Role role, int pageNum, int pageSize, String sortField,
      String sortDir);

  Page<User> searchAllByEmail(String keyword, int pageNum, int pageSize,
      String sortField,
      String sortDir);

  Page<User> searchAllByEmailAndRole(String keyword, Role role, int pageNum,
      int pageSize,
      String sortField, String sortDir);

  User registerUser(UserRegisterDTO user, String password);

  void createUser(UserDTO userDTO);

  void updateUser(UserUpdateDTO user);

  void updateInfoUser(User user, String name, String password);

  void removeUserById(User user);

  User getUserByEmail(String email);

  Optional<User> getUserWithRolesByEmail(String email);

  Optional<User> getUserById(int id);

  long countByRole(String role);

  String resetPasswordByAdmin(User user);

//    Optional<User> activateRegistration(String key);

  Optional<User> completePasswordReset(String newPassword, String key);

  Optional<User> requestPasswordReset(String mail);
}
