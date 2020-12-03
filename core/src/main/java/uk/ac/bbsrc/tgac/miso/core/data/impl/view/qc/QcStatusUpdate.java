package uk.ac.bbsrc.tgac.miso.core.data.impl.view.qc;

public class QcStatusUpdate {

  private QcNodeType entityType;
  private Long id;
  private Long[] ids; // used for runPartitions and runPartitionAliquots. IDs are in same order as entity type name
  private Boolean qcPassed;
  private Long qcStatusId; // may be detailedQcStatusId or partitionQcTypeId
  private String qcNote;

  public QcNodeType getEntityType() {
    return entityType;
  }

  public void setEntityType(QcNodeType entityType) {
    this.entityType = entityType;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long[] getIds() {
    return ids;
  }

  public void setIds(Long[] ids) {
    this.ids = ids;
  }

  public Boolean getQcPassed() {
    return qcPassed;
  }

  public void setQcPassed(Boolean qcPassed) {
    this.qcPassed = qcPassed;
  }

  public Long getQcStatusId() {
    return qcStatusId;
  }

  public void setQcStatusId(Long qcStatusId) {
    this.qcStatusId = qcStatusId;
  }

  public String getQcNote() {
    return qcNote;
  }

  public void setQcNote(String qcNote) {
    this.qcNote = qcNote;
  }

}
