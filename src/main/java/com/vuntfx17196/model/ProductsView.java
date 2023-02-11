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

  public int getProductId() {
    return id;
  }

  public int getRanks() {
    return count;
  }

  public String getProductTitle() {
    return title;
  }
}
