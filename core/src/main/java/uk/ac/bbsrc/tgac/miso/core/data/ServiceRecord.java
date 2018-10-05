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

import com.eaglegenomics.simlims.core.SecurityProfile;

import uk.ac.bbsrc.tgac.miso.core.data.impl.FileAttachment;
import uk.ac.bbsrc.tgac.miso.core.data.impl.InstrumentImpl;

@Entity
@Table(name = "ServiceRecord")
public class ServiceRecord implements Serializable, Deletable, Attachable {

  private static final long serialVersionUID = 1L;

  public static final long UNSAVED_ID = 0L;
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long recordId = ServiceRecord.UNSAVED_ID;

  @ManyToOne(targetEntity = InstrumentImpl.class)
  @JoinColumn(name = "instrumentId")
  private Instrument instrument;

  @Column(nullable = false)
  private String title;
  private String details;

  @Column(name = "servicedBy", nullable = false)
  private String servicedByName;
  private String referenceNumber;

  @Column(nullable = false)
  @Temporal(TemporalType.DATE)
  private Date serviceDate;

  @Temporal(TemporalType.TIMESTAMP)
  private Date shutdownTime;

  @Temporal(TemporalType.TIMESTAMP)
  private Date restoredTime;

  @OneToMany(targetEntity = FileAttachment.class, cascade = CascadeType.ALL)
  @JoinTable(name = "ServiceRecord_Attachment", joinColumns = { @JoinColumn(name = "recordId") }, inverseJoinColumns = {
      @JoinColumn(name = "attachmentId") })
  private List<FileAttachment> attachments;

  @Override
  public void setId(long id) {
    this.recordId = id;
  }

  @Override
  public long getId() {
    return recordId;
  }

  public void setInstrument(Instrument instrument) {
    this.instrument = instrument;
  }

  public Instrument getInstrument() {
    return instrument;
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

  public void setShutdownTime(Date date) {
    this.shutdownTime = date;
  }

  public Date getShutdownTime() {
    return shutdownTime;
  }

  public void setRestoredTime(Date date) {
    this.restoredTime = date;
  }

  public Date getRestoredTime() {
    return restoredTime;
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
  public String getDeleteType() {
    return "Service Record";
  }

  @Override
  public String getDeleteDescription() {
    return getInstrument().getName() + " "
        + (getReferenceNumber() == null ? "" : "RE " + getReferenceNumber() + ": ")
        + getTitle();
  }

  @Override
  public SecurityProfile getDeletionSecurityProfile() {
    return null;
  }
}
