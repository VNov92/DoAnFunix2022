package com.vuntfx17196.dto;

import com.vuntfx17196.global.PasswordMatches;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

@PasswordMatches(password = "newPassword", passwordConfirm = "confirmNewPassword")
public class PasswordChangeDTO implements Serializable {
    @NotBlank
    private String currentPassword;
    @NotBlank
    @Size(min = 8, max = 20)
    private String newPassword;
    @NotBlank
    @Size(min = 8, max = 20)
    private String confirmNewPassword;

    public PasswordChangeDTO() {
        // Empty constructor needed for Jackson.
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
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
