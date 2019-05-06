package uk.ac.bbsrc.tgac.miso.dto;

public class ProjectDto {
  private long id;
  private String alias;
  private String name;
  private String creationDate;
  private String shortName;
  private String description;
  private String progress;
  private Long referenceGenomeId;
  private String defaultSciName;
  private Long defaultTargetedSequencingId;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(String creationDate) {
    this.creationDate = creationDate;
  }

  public String getShortName() {
    return shortName;
  }

  public void setShortName(String shortName) {
    this.shortName = shortName;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getProgress() {
    return progress;
  }

  public void setProgress(String progress) {
    this.progress = progress;
  }

  public Long getReferenceGenomeId() {
    return referenceGenomeId;
  }

  public void setReferenceGenomeId(Long referenceGenomeId) {
    this.referenceGenomeId = referenceGenomeId;
  }

  public String getDefaultSciName() {
    return defaultSciName;
  }

  public void setDefaultSciName(String defaultSciName) {
    this.defaultSciName = defaultSciName;
  }

  public Long getDefaultTargetedSequencingId() {
    return defaultTargetedSequencingId;
  }

  public void setDefaultTargetedSequencingId(Long defaultTargetedSequencingId) {
    this.defaultTargetedSequencingId = defaultTargetedSequencingId;
  }
}
