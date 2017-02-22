package uk.ac.bbsrc.tgac.miso.dto;

public class QcDto {

  private Long id;
  private String qcCreator;
  private QcTypeDto qcType;
  private String qcDate;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getQcCreator() {
    return qcCreator;
  }

  public void setQcCreator(String qcCreator) {
    this.qcCreator = qcCreator;
  }

  public QcTypeDto getQcType() {
    return qcType;
  }

  public void setQcType(QcTypeDto qcType) {
    this.qcType = qcType;
  }

  public String getQcDate() {
    return qcDate;
  }

  public void setQcDate(String qcDate) {
    this.qcDate = qcDate;
  }

}
