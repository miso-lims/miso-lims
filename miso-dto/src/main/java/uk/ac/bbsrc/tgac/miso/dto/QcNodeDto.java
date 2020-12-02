package uk.ac.bbsrc.tgac.miso.dto;

public class QcNodeDto {

  private String entityType;
  private String typeLabel;
  private Long id;
  private Long[] ids; // used for runPartitions and runPartitionAliquots. IDs are in same order as entity type name
  private String name;
  private String label;
  private Boolean qcPassed;
  private Long qcStatusId; // may be detailedQcStatusId or partitionQcTypeId
  private String qcNote;

  public String getEntityType() {
    return entityType;
  }

  public void setEntityType(String entityType) {
    this.entityType = entityType;
  }

  public String getTypeLabel() {
    return typeLabel;
  }

  public void setTypeLabel(String typeLabel) {
    this.typeLabel = typeLabel;
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
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
