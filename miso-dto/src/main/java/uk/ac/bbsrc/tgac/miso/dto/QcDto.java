package uk.ac.bbsrc.tgac.miso.dto;

public class QcDto {

  private String creator;
  private String date;
  private String entityAlias;
  private Long entityId;
  private Long id;
  private Double results;
  private QcTypeDto type;
  private String description;

  public String getCreator() {
    return creator;
  }

  public String getDate() {
    return date;
  }

  public String getEntityAlias() {
    return entityAlias;
  }

  public Long getEntityId() {
    return entityId;
  }

  public Long getId() {
    return id;
  }

  public Double getResults() {
    return results;
  }

  public QcTypeDto getType() {
    return type;
  }

  public String getDescription() {
    return description;
  }

  public void setCreator(String creator) {
    this.creator = creator;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public void setEntityAlias(String entityAlias) {
    this.entityAlias = entityAlias;
  }

  public void setEntityId(Long entityId) {
    this.entityId = entityId;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setResults(Double results) {
    this.results = results;
  }

  public void setType(QcTypeDto type) {
    this.type = type;
  }

  public void setDescription(String description) {
    this.description = description;
  }

}
