package uk.ac.bbsrc.tgac.miso.dto;

public class QcControlRunDto {

  private Long id;
  private Long controlId;
  private String lot;
  private Boolean qcPassed;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getControlId() {
    return controlId;
  }

  public void setControlId(Long controlId) {
    this.controlId = controlId;
  }

  public String getLot() {
    return lot;
  }

  public void setLot(String lot) {
    this.lot = lot;
  }

  public Boolean getQcPassed() {
    return qcPassed;
  }

  public void setQcPassed(Boolean qcPassed) {
    this.qcPassed = qcPassed;
  }

}
