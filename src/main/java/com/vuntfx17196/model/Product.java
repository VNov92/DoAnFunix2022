package com.vuntfx17196.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Entity
@Table(name = "products")
public class Product implements Serializable {

  @Serial
  private static final long serialVersionUID = -8824535171976606454L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  @Column(nullable = false, unique = true)
  @NotBlank
  private String title;
  @Column(name = "short_detail", columnDefinition = "text not null")
  @NotBlank
  private String shortDetail;
  @Column(name = "full_detail", columnDefinition = "text not null")
  @NotBlank
  private String fullDetail;
  private String ggId;
  @Column(name = "image_thumbnail")
  private String imageThumbnail;
  private int cost;
  @Column(name = "view_times")
  private long viewTimes;
  @CreatedDate
  @Column(name = "create_date", updatable = false)
  private Instant createdDate = Instant.now();
  @LastModifiedDate
  @Column(name = "last_modified_date")
  private Instant lastModifiedDate = Instant.now();
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id", referencedColumnName = "ID")
  private Category category;

  public Product() {
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

  public int getCost() {
    return cost;
  }

  public void setCost(int cost) {
    this.cost = cost;
  }

  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  public Instant getCreatedDate() {
    return createdDate;
  }

  public void setCreatedDate(Instant createdDate) {
    this.createdDate = createdDate;
  }

  public Instant getLastModifiedDate() {
    return lastModifiedDate;
  }

  public void setLastModifiedDate(Instant lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }

  public long getViewTimes() {
    return viewTimes;
  }

  public void setViewTimes(long viewTimes) {
    this.viewTimes = viewTimes;
  }

  public String getGgId() {
    return ggId;
  }

  public void setGgId(String ggId) {
    this.ggId = ggId;
  }


  public String getImageThumbnail() {
    return imageThumbnail;
  }

  public void setImageThumbnail(String imageThumbnail) {
    this.imageThumbnail = imageThumbnail;
  }
}
