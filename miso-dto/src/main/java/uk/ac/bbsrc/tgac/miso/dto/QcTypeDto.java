package uk.ac.bbsrc.tgac.miso.dto;

public class QcTypeDto {

  private Long id;
  private String name;
  private String description;
  private String qcTarget;
  private String units;
  private Integer precisionAfterDecimal;
  private boolean archived;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getQcTarget() {
    return qcTarget;
  }

  public void setQcTarget(String qcTarget) {
    this.qcTarget = qcTarget;
  }

  public String getUnits() {
    return units;
  }

  public void setUnits(String units) {
    this.units = units;
  }

  public Integer getPrecisionAfterDecimal() {
    return precisionAfterDecimal;
  }

  public void setPrecisionAfterDecimal(Integer precisionAfterDecimal) {
    this.precisionAfterDecimal = precisionAfterDecimal;
  }

  public boolean isArchived() {
    return archived;
  }

  public void setArchived(boolean archived) {
    this.archived = archived;
  }

}
