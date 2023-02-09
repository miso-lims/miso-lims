package uk.ac.bbsrc.tgac.miso.dto;

import java.util.List;

public class ServiceRecordDto {

  private Long id;
  private String serviceDate;
  private String title;
  private String details;
  private String referenceNumber;
  private Long positionId;
  private String position;
  private String servicedBy;
  private Boolean outOfService;
  private String startTime;
  private String endTime;
  private List<AttachmentDto> attachments;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getServiceDate() {
    return serviceDate;
  }

  public void setServiceDate(String serviceDate) {
    this.serviceDate = serviceDate;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDetails() {
    return details;
  }

  public void setDetails(String details) {
    this.details = details;
  }

  public String getReferenceNumber() {
    return referenceNumber;
  }

  public void setReferenceNumber(String referenceNumber) {
    this.referenceNumber = referenceNumber;
  }

  public Long getPositionId() {
    return positionId;
  }

  public void setPositionId(Long positionId) {
    this.positionId = positionId;
  }

  public String getPosition() {
    return position;
  }

  public void setPosition(String position) {
    this.position = position;
  }

  public String getServicedBy() {
    return servicedBy;
  }

  public void setServicedBy(String servicedBy) {
    this.servicedBy = servicedBy;
  }

  public Boolean getOutOfService() {
    return outOfService;
  }

  public void setOutOfService(Boolean outOfService) {
    this.outOfService = outOfService;
  }

  public String getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }

  public String getEndTime() {
    return endTime;
  }

  public void setEndTime(String endTime) {
    this.endTime = endTime;
  }

  public List<AttachmentDto> getAttachments() {
    return attachments;
  }

  public void setAttachments(List<AttachmentDto> attachments) {
    this.attachments = attachments;
  }

}
