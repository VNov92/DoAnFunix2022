package com.vuntfx17196.dto;

import com.vuntfx17196.model.Role;
import com.vuntfx17196.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

public class UserUpdateDTO implements Serializable {
    private Integer id;
    @NotBlank
    private String name;
    @NotNull
    private Integer points;
    @NotNull
    private boolean activated = false;
    @NotEmpty
    private List<Integer> roleIds;
    private Instant lastModifiedDate = Instant.now();

    public UserUpdateDTO() {
    }

    public UserUpdateDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.points = user.getPoints();
        this.activated = user.isActivated();
        this.roleIds = user.getRoles().stream().map(Role::getId).toList();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public List<Integer> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Integer> roleIds) {
        this.roleIds = roleIds;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
