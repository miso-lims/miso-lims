package uk.ac.bbsrc.tgac.miso.dto;

public class GroupDto {
  private String description;
  private long id;
  private String name;

  public String getDescription() {
    return description;
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setId(long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

}
