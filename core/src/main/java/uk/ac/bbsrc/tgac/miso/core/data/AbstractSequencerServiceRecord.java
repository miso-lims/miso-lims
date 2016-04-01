package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Date;

public abstract class AbstractSequencerServiceRecord implements SequencerServiceRecord {
  
  public static final long UNSAVED_ID = 0L;
  
  private long id = AbstractSequencerServiceRecord.UNSAVED_ID;
  private SequencerReference sequencerReference;
  private String title;
  private String details;
  private String servicedByName;
  private String referenceNumber;
  private Date serviceDate;
  private Date shutdownTime;
  private Date restoredTime;

  @Override
  public void setId(long id) {
    this.id = id;
  }

  @Override
  public long getId() {
    return id;
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
