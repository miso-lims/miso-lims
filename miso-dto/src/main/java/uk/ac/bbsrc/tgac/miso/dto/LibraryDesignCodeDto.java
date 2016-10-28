package uk.ac.bbsrc.tgac.miso.dto;

public class LibraryDesignCodeDto {

  private Long id;
  private String code;
  private String description;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String toString() {
    return "LibraryDesignCodeDto [id=" + id + ", code=" + code + ", description=" + description + "]";
  }

}
