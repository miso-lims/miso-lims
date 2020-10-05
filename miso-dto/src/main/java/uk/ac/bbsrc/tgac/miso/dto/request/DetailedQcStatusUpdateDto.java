package uk.ac.bbsrc.tgac.miso.dto.request;

public class DetailedQcStatusUpdateDto {

  private Long qcStatusId;
  private String note;

  public Long getQcStatusId() {
    return qcStatusId;
  }

  public void setQcStatusId(Long qcStatusId) {
    this.qcStatusId = qcStatusId;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

}
