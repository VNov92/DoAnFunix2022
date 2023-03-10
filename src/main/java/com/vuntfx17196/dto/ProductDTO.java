package com.vuntfx17196.dto;

import com.vuntfx17196.model.Product;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.Instant;

public class ProductDTO implements Serializable {

  private int id;
  @NotBlank
  private String title;
  @NotBlank
  private String shortDetail;
  @NotBlank
  private String fullDetail;
  private String imageThumbnail;
  private String ggId;

  @Min(value = 0)
  private int cost;
  @Min(value = 1, message = "Choose one of categories")
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
    this.ggId = product.getGgId();
    this.imageThumbnail = product.getImageThumbnail();
    this.cost = product.getCost();
    this.categoryId = product.getCategory().getId();
    this.createdDate = product.getCreatedDate();
    this.lastModifiedDate = product.getLastModifiedDate();
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

  public int getId() {
    return id;
  }

  public int getCost() {
    return cost;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getGgId() {
    return ggId;
  }

  public void setGgId(String ggId) {
    this.ggId = ggId;
  }

  public void setCost(int cost) {
    this.cost = cost;
  }

  public String getImageThumbnail() {
    return imageThumbnail;
  }

  public void setImageThumbnail(String imageThumbnail) {
    this.imageThumbnail = imageThumbnail;
  }
}
