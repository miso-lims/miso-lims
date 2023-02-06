package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import uk.ac.bbsrc.tgac.miso.core.data.impl.FileAttachment;

@Entity
@Table(name = "ServiceRecord")
public class ServiceRecord implements Serializable, Deletable, Attachable {

  private static final long serialVersionUID = 1L;

  private static final long UNSAVED_ID = 0L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long recordId = ServiceRecord.UNSAVED_ID;

  @Column(nullable = false)
  private String title;
  private String details;

  @Column(name = "servicedBy")
  private String servicedByName;
  private String referenceNumber;

  @Column(nullable = false)
  @Temporal(TemporalType.DATE)
  private Date serviceDate;

  @Temporal(TemporalType.TIMESTAMP)
  private Date startTime;

  @Temporal(TemporalType.TIMESTAMP)
  private Date endTime;

  private boolean outOfService = true;

  @ManyToOne
  @JoinColumn(name = "positionId")
  private InstrumentPosition position;

  @OneToMany(targetEntity = FileAttachment.class, cascade = CascadeType.ALL)
  @JoinTable(name = "ServiceRecord_Attachment", joinColumns = {@JoinColumn(name = "recordId")}, inverseJoinColumns = {
      @JoinColumn(name = "attachmentId")})
  private List<FileAttachment> attachments;

  @Transient
  private List<FileAttachment> pendingAttachmentDeletions;

  @Override
  public void setId(long id) {
    this.recordId = id;
  }

  @Override
  public long getId() {
    return recordId;
  }

  public boolean isSaved() {
    return getId() != UNSAVED_ID;
  }


  public void setTitle(String title) {
    this.title = title;
  }

  public String getTitle() {
    return title;
  }

  public void setDetails(String details) {
    this.details = details;
  }

  public String getDetails() {
    return details;
  }

  public void setServicedByName(String servicer) {
    this.servicedByName = servicer;
  }

  public String getServicedByName() {
    return this.servicedByName;
  }

  public void setReferenceNumber(String referenceNumber) {
    this.referenceNumber = referenceNumber;
  }

  public String getReferenceNumber() {
    return referenceNumber;
  }

  public void setServiceDate(Date date) {
    this.serviceDate = date;
  }

  public Date getServiceDate() {
    return serviceDate;
  }

  public void setStartTime(Date date) {
    this.startTime = date;
  }

  public Date getStartTime() {
    return startTime;
  }

  public void setEndTime(Date date) {
    this.endTime = date;
  }

  public Date getEndTime() {
    return endTime;
  }

  public void setOutOfService(boolean outOfService) {
    this.outOfService = outOfService;
  }

  public boolean isOutOfService() {
    return this.outOfService;
  }

  public InstrumentPosition getPosition() {
    return position;
  }

  public void setPosition(InstrumentPosition position) {
    this.position = position;
  }

  @Override
  public List<FileAttachment> getAttachments() {
    return attachments;
  }

  @Override
  public void setAttachments(List<FileAttachment> attachments) {
    this.attachments = attachments;
  }

  @Override
  public String getAttachmentsTarget() {
    return "servicerecord";
  }

  @Override
  public List<FileAttachment> getPendingAttachmentDeletions() {
    return pendingAttachmentDeletions;
  }

  @Override
  public void setPendingAttachmentDeletions(List<FileAttachment> pendingAttachmentDeletions) {
    this.pendingAttachmentDeletions = pendingAttachmentDeletions;
  }

  @Override
  public String getDeleteType() {
    return "Service Record";
  }

  @Override
  public String getDeleteDescription() {
    // TODO
    return (getReferenceNumber() == null ? "" : "RE " + getReferenceNumber() + ": ") + getTitle();
  }

}
