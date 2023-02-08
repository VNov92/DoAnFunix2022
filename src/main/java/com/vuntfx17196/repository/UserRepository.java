package com.vuntfx17196.repository;

import com.vuntfx17196.model.Role;
import com.vuntfx17196.model.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

  User findByEmail(String email);

  @EntityGraph(attributePaths = "roles")
  Optional<User> findOneWithRolesByEmailIgnoreCase(String email);

  long countByRoles(Role role);

  Page<User> findAll(Pageable pageable);

  Page<User> findAllByRoles(Role role, Pageable pageable);

  Page<User> searchAllByEmailContainingIgnoreCaseOrNameContainingIgnoreCase(String email,
      String name,
      Pageable pageable);

  Page<User> searchAllByEmailContainingIgnoreCaseAndRolesOrNameContainingIgnoreCaseAndRoles(
      String email, Role role, String name,
      Role role1, Pageable pageable);

  Optional<User> findOneByActivationKey(String key);

  Optional<User> findOneByResetKey(String key);

  Optional<User> findOneByEmailIgnoreCase(String mail);
}
