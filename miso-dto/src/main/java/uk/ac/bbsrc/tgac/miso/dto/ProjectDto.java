package uk.ac.bbsrc.tgac.miso.dto;

public class ProjectDto {
  private long id;
  private String title;
  private String name;
  private String creationDate;
  private String code;
  private String description;
  private String status;
  private Long referenceGenomeId;
  private String defaultSciName;
  private Long defaultTargetedSequencingId;
  private Long pipelineId;
  private boolean secondaryNaming;
  private String rebNumber;
  private String rebExpiry;
  private Integer samplesExpected;
  private Long contactId;
  private String contactName;
  private String contactEmail;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
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

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
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

  public Long getPipelineId() {
    return pipelineId;
  }

  public void setPipelineId(Long pipelineId) {
    this.pipelineId = pipelineId;
  }

  public boolean isSecondaryNaming() {
    return secondaryNaming;
  }

  public void setSecondaryNaming(boolean secondaryNaming) {
    this.secondaryNaming = secondaryNaming;
  }

  public String getRebNumber() {
    return rebNumber;
  }

  public void setRebNumber(String rebNumber) {
    this.rebNumber = rebNumber;
  }

  public String getRebExpiry() {
    return rebExpiry;
  }

  public void setRebExpiry(String rebExpiry) {
    this.rebExpiry = rebExpiry;
  }

  public Integer getSamplesExpected() {
    return samplesExpected;
  }

  public void setSamplesExpected(Integer samplesExpected) {
    this.samplesExpected = samplesExpected;
  }

  public Long getContactId() {
    return contactId;
  }

  public void setContactId(Long contactId) {
    this.contactId = contactId;
  }

  public String getContactName() {
    return contactName;
  }

  public void setContactName(String contactName) {
    this.contactName = contactName;
  }

  public String getContactEmail() {
    return contactEmail;
  }

  public void setContactEmail(String contactEmail) {
    this.contactEmail = contactEmail;
  }
}
