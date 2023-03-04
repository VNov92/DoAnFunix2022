package com.vuntfx17196.dto;

import com.vuntfx17196.model.Product;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.Instant;

public class ProductDTO implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 4131218893855361903L;
    private Integer id = 0;
    @NotBlank
    private String title;
    @NotBlank
    private String shortDetail;
    @NotBlank
    private String fullDetail;
    private String url;
    @NotNull
    @Min(value = 0)
    private int cost;
    @NotNull
    @Min(value = 1)
    private int categoryId;
    private Instant createdDate;
    private Instant lastModifiedDate;

    public ProductDTO() {
    }

    public ProductDTO(Product product) {
        this.id = product.getId();
        this.title = product.getTitle();
        this.shortDetail = product.getShortDetail();
        this.fullDetail = product.getFullDetail();
        this.url = product.getUrl();
        this.cost = product.getCost();
        this.categoryId = product.getCategory().getId();
        this.createdDate = product.getCreatedDate();
        this.lastModifiedDate = product.getLastModifiedDate();
    }

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

    public String getShortDetail() {
        return shortDetail;
    }

    public void setShortDetail(String shortDetail) {
        this.shortDetail = shortDetail;
    }

    public String getFullDetail() {
        return fullDetail;
    }

    public void setFullDetail(String fullDetail) {
        this.fullDetail = fullDetail;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

}
