package com.vuntfx17196.dto;

import com.vuntfx17196.global.PasswordMatches;
import com.vuntfx17196.model.Role;
import com.vuntfx17196.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@PasswordMatches(password = "password", passwordConfirm = "matchingPassword")
public class UserDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -2736455610177598326L;
    private Integer id = 0;
    @NotBlank
    @Email(message = "Invalid email")
    private String email;
    @NotBlank
    private String name;
    @NotBlank
    @Size(min = 8, max = 20, message = "Length of password must at less 8  characters")
    private String password;
    @NotBlank
    private String matchingPassword;
    private Integer points;
    @NotEmpty
    private List<Integer> roleIds;
    private boolean activated = false;
    private Instant createdDate;
    private Instant lastModifiedDate;

    public UserDTO() {
    }

    public UserDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
        this.points = user.getPoints();
        this.roleIds = user.getRoles().stream().map(Role::getId).toList();
        this.activated = user.isActivated();
        this.createdDate = user.getCreatedDate();
        this.lastModifiedDate = user.getLastModifiedDate();
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Integer> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Integer> roleIds) {
        this.roleIds = roleIds;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getMatchingPassword() {
        return matchingPassword;
    }

    public void setMatchingPassword(String matchingPassword) {
        this.matchingPassword = matchingPassword;
    }

}
