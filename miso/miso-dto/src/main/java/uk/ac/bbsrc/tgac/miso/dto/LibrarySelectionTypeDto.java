package uk.ac.bbsrc.tgac.miso.dto;

public class LibrarySelectionTypeDto {
  private String alias;
  private long id;
  private String name;

  public String getAlias() {
    return alias;
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public void setId(long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

}
