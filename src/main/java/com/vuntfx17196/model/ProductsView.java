package com.vuntfx17196.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.io.Serializable;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
public class ProductsView implements Serializable {

  @Id
  @Column(name = "product_id")
  private int id;
  private int count;

  private String title;
  private String url;
  @Column(name = "short_detail")
  private String shortDetail;

  public int getId() {
    return id;
  }

  public int getCount() {
    return count;
  }

  public String getTitle() {
    return title;
  }

  public String getUrl() {
    return url;
  }

  public String getShortDetail() {
    return shortDetail;
  }
}
