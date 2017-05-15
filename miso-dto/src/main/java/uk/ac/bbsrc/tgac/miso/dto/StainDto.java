package uk.ac.bbsrc.tgac.miso.dto;

public class StainDto {
  private String category;
  private long id;
  private String name;

  public String getCategory() {
    return category;
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public void setId(long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }
}
