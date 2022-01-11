package uk.ac.bbsrc.tgac.miso.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubprojectDto {

  private Long id;
  private String alias;
  private String description;
  private Long parentProjectId;
  private Boolean priority;
  private Long referenceGenomeId;

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

  public Long getParentProjectId() {
    return parentProjectId;
  }

  public void setParentProjectId(Long parentProjectId) {
    this.parentProjectId = parentProjectId;
  }

  public Boolean getPriority() {
    return priority;
  }

  public void setPriority(Boolean priority) {
    this.priority = priority;
  }

  @Override
  public String toString() {
    return "SubprojectDto [id=" + id + ", alias=" + alias + ", description=" + description + ", parentProjectId=" + parentProjectId
        + ", priority=" + priority + "]";
  }

  public Long getReferenceGenomeId() {
    return referenceGenomeId;
  }

  public void setReferenceGenomeId(Long referenceGenomeId) {
    this.referenceGenomeId = referenceGenomeId;
  }

}
