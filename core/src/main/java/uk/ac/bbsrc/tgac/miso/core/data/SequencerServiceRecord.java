package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Date;

public interface SequencerServiceRecord extends Deletable {
  
  public void setId(long id);
  
  public long getId();
  
  /**
   * Sets the sequencer reference that the service was performed on
   * 
   * @param sequencer
   */
  public void setSequencerReference(SequencerReference sequencer);
  
  /**
   * @return the sequencer reference that the service was performed on
   */
  public SequencerReference getSequencerReference();
  
  /**
   * Sets the title of this service record's message
   * 
   * @param title
   */
  public void setTitle(String title);
  
  /**
   * @return the title of this service record's message
   */
  public String getTitle();
  
  /**
   * Sets the detail text of this service record's message
   * 
   * @param details
   */
  public void setDetails(String details);
  
  /**
   * @return the detail text of this service record's message
   */
  public String getDetails();
  
  /**
   * Sets the name of the person who performed the service
   * 
   * @param servicer
   */
  public void setServicedByName(String servicer);
  
  /**
   * @return the name of the person who performed the service
   */
  public String getServicedByName();
  
  /**
   * Sets the service reference number, which may indicate an external service id, a phone number, or other source of 
   * additional information
   * 
   * @param referenceNumber
   */
  public void setReferenceNumber(String referenceNumber);
  
  /**
   * @return the service reference number, which may indicate an external service id, a phone number, or other source of 
   * additional information
   */
  public String getReferenceNumber();
  
  /**
   * Sets the date that this service was performed
   * 
   * @param date
   */
  public void setServiceDate(Date date);
  
  /**
   * @return the date that this service was performed
   */
  public Date getServiceDate();
  
  /**
   * Sets the time that the sequencer was taken offline for servicing
   * 
   * @param date
   */
  public void setShutdownTime(Date date);
  
  /**
   * @return the time that the sequencer was taken offline for servicing
   */
  public Date getShutdownTime();
  
  /**
   * Sets the time that the sequencer was brought back online after servicing
   * 
   * @param date
   */
  public void setRestoredTime(Date date);
  
  /**
   * @return the time that the sequencer was brought back online after servicing
   */
  public Date getRestoredTime();
  
}
