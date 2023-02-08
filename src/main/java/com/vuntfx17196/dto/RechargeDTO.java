package com.vuntfx17196.dto;

import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.time.Instant;

public class RechargeDTO implements Serializable {
    private Integer id;
    @NotBlank
    @Email
    private String email;
    @NotNull
    @Min(value = 10000)
    private int money;
    private Instant createdDate = Instant.now();
    @NotBlank
    @Pattern(regexp = "success|failed")
    private String status;

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

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
