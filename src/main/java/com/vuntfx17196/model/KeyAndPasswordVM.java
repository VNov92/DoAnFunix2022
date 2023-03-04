package com.vuntfx17196.model;

import com.vuntfx17196.global.PasswordMatches;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

@PasswordMatches(password = "newPassword", passwordConfirm = "confirmNewPassword")
public class KeyAndPasswordVM implements Serializable {

    private String key;

    @NotBlank
    @Size(min = 8, max = 100)
    private String newPassword;
    @NotBlank
    private String confirmNewPassword;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmNewPassword() {
        return confirmNewPassword;
    }

    public void setConfirmNewPassword(String confirmNewPassword) {
        this.confirmNewPassword = confirmNewPassword;
    }
}
