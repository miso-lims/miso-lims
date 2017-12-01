package uk.ac.bbsrc.tgac.miso.dto;

public class LibraryTypeDto {
  private String alias;
  private boolean archived;
  private long id;
  private String platform;

  public String getAlias() {
    return alias;
  }

  public long getId() {
    return id;
  }

  public String getPlatform() {
    return platform;
  }

  public boolean isArchived() {
    return archived;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public void setArchived(boolean archived) {
    this.archived = archived;
  }

  public void setId(long id) {
    this.id = id;
  }

  public void setPlatform(String platform) {
    this.platform = platform;
  }
}
