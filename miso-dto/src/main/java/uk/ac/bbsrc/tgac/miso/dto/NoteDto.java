package uk.ac.bbsrc.tgac.miso.dto;

public class NoteDto {
  private Long id;
  private String text;
  private boolean internalOnly;
  private String creationDate;
  private String ownerName;
  private String entityName;
  private String entityAlias;
  private Long entityId;
  private String entityType;
  private String source;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public boolean isInternalOnly() {
    return internalOnly;
  }

  public void setInternalOnly(boolean internalOnly) {
    this.internalOnly = internalOnly;
  }

  public String getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(String creationDate) {
    this.creationDate = creationDate;
  }

  public String getOwnerName() {
    return ownerName;
  }

  public void setOwnerName(String ownerName) {
    this.ownerName = ownerName;
  }

  public String getEntityName() {
    return entityName;
  }

  public void setEntityName(String entityName) {
    this.entityName = entityName;
  }

  public String getEntityAlias() {
    return entityAlias;
  }

  public void setEntityAlias(String entityAlias) {
    this.entityAlias = entityAlias;
  }

  public Long getEntityId() {
    return entityId;
  }

  public void setEntityId(Long entityId) {
    this.entityId = entityId;
  }

  public String getEntityType() {
    return entityType;
  }

  public void setEntityType(String entityType) {
    this.entityType = entityType;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }
}
