package uk.ac.bbsrc.tgac.miso.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TissueOriginDto {

  private Long id;
  private String alias;
  private String description;
  private String label;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  @Override
  public String toString() {
    return "TissueOriginDto [id=" + id + ", alias=" + alias + ", description=" + description + ", label=" + label
        + "]";
  }

}
