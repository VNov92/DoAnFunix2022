package com.vuntfx17196.dto;

import java.io.Serializable;

public class GoogleDriveFileDTO implements Serializable {

  private String id;
  private String name;
  private String link;
  private String size;
  private String thumbnailLink;
  private boolean shared;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public String getSize() {
    return size;
  }

  public void setSize(String size) {
    this.size = size;
  }

  public String getThumbnailLink() {
    return thumbnailLink;
  }

  public void setThumbnailLink(String thumbnailLink) {
    this.thumbnailLink = thumbnailLink;
  }

  public boolean isShared() {
    return shared;
  }

  public void setShared(boolean shared) {
    this.shared = shared;
  }
}
