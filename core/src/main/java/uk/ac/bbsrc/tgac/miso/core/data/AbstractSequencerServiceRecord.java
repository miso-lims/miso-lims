package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerReferenceImpl;

@MappedSuperclass
public abstract class AbstractSequencerServiceRecord implements SequencerServiceRecord, Serializable {
  
  private static final long serialVersionUID = 1L;

  public static final long UNSAVED_ID = 0L;
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long recordId = AbstractSequencerServiceRecord.UNSAVED_ID;

  @ManyToOne(targetEntity = SequencerReferenceImpl.class)
  @JoinColumn(name = "sequencerReferenceId")
  private SequencerReference sequencerReference;

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

  @Override
  public void setId(long id) {
    this.recordId = id;
  }

  @Override
  public long getId() {
    return recordId;
  }

  @Override
  public void setSequencerReference(SequencerReference sequencer) {
    this.sequencerReference = sequencer;
  }

  @Override
  public SequencerReference getSequencerReference() {
    return sequencerReference;
  }

  @Override
  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public String getTitle() {
    return title;
  }

  @Override
  public void setDetails(String details) {
    this.details = details;
  }

  @Override
  public String getDetails() {
    return details;
  }

  @Override
  public void setServicedByName(String servicer) {
    this.servicedByName = servicer;
  }

  @Override
  public String getServicedByName() {
    return this.servicedByName;
  }

  @Override
  public void setReferenceNumber(String referenceNumber) {
    this.referenceNumber = referenceNumber;
  }

  @Override
  public String getReferenceNumber() {
    return referenceNumber;
  }

  @Override
  public void setServiceDate(Date date) {
    this.serviceDate = date;
  }

  @Override
  public Date getServiceDate() {
    return serviceDate;
  }

  @Override
  public void setShutdownTime(Date date) {
    this.shutdownTime = date;
  }

  @Override
  public Date getShutdownTime() {
    return shutdownTime;
  }

  @Override
  public void setRestoredTime(Date date) {
    this.restoredTime = date;
  }

  @Override
  public Date getRestoredTime() {
    return restoredTime;
  }
  
  @Override
  public boolean isDeletable() {
    return getId() != AbstractSequencerReference.UNSAVED_ID;
  }

}
