package uk.ac.bbsrc.tgac.miso.dto;

import java.util.List;

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
  private List<ProjectContactDto> contacts;
  private List<Long> assayIds;
  private String additionalDetails;
  private List<Long> deliverableIds;

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

  public List<ProjectContactDto> getContacts() {
    return contacts;
  }

  public void setContacts(List<ProjectContactDto> contacts) {
    this.contacts = contacts;
  }

  public List<Long> getAssayIds() {
    return assayIds;
  }

  public void setAssayIds(List<Long> assayIds) {
    this.assayIds = assayIds;
  }

  public String getAdditionalDetails() {
    return additionalDetails;
  }

  public void setAdditionalDetails(String additionalDetails) {
    this.additionalDetails = additionalDetails;
  }

  public List<Long> getDeliverableIds() {
    return deliverableIds;
  }

  public void setDeliverableIds(List<Long> deliverableIds) {
    this.deliverableIds = deliverableIds;
  }
}
