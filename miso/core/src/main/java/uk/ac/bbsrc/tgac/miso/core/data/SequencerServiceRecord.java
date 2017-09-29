package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import uk.ac.bbsrc.tgac.miso.core.data.impl.SequencerReferenceImpl;

@Entity
@Table(name = "SequencerServiceRecord")
public class SequencerServiceRecord implements Serializable, Deletable {

  private static final long serialVersionUID = 1L;

  public static final long UNSAVED_ID = 0L;
  
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long recordId = SequencerServiceRecord.UNSAVED_ID;

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

  public void setId(long id) {
    this.recordId = id;
  }

  public long getId() {
    return recordId;
  }

  public void setSequencerReference(SequencerReference sequencer) {
    this.sequencerReference = sequencer;
  }

  public SequencerReference getSequencerReference() {
    return sequencerReference;
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
  public boolean isDeletable() {
    return getId() != AbstractSequencerReference.UNSAVED_ID;
  }
}
