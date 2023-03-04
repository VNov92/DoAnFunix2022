package com.vuntfx17196.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

@Entity
@Table(name = "Categories")
public class Category implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 7546064649036680959L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id = 0;
    @NotBlank
    @Column(nullable = false, unique = true)
    private String title;
    @NotBlank
    @Column(nullable = false)
    private String description;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
