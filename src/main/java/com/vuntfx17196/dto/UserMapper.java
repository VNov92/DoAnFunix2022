package com.vuntfx17196.dto;

import com.vuntfx17196.model.User;
import com.vuntfx17196.repository.RoleRepository;

import java.util.Optional;

public class UserMapper {
    private RoleRepository roleRepository;

    public UserMapper(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public User userDTOToUser(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        } else {
            User user = new User();
            user.setId(userDTO.getId());
            user.setEmail(userDTO.getEmail());
            user.setName(userDTO.getName());
            user.setPassword(userDTO.getPassword());
            user.setPoints(userDTO.getPoints());
            user.setActivated(userDTO.isActivated());
            user.setRoles(userDTO.getRoleIds().stream().map(roleRepository::findById).filter(Optional::isPresent).map(Optional::get).toList());
            return user;
        }
    }
}
