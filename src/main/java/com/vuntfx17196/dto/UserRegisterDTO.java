package com.vuntfx17196.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

/**
 * A DTO representing a name and email of new user.
 */
public class UserRegisterDTO implements Serializable {
    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String email;

    public UserRegisterDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
