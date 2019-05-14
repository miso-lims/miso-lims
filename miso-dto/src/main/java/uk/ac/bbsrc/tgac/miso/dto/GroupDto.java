package uk.ac.bbsrc.tgac.miso.dto;

public class GroupDto {
  private String description;
  private Long id;
  private String name;

  public String getDescription() {
    return description;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

}
