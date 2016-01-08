package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(typing = JsonSerialize.Typing.STATIC, include = JsonSerialize.Inclusion.NON_NULL)
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
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
   * Sets the contact number for this service
   * 
   * @param phone
   */
  public void setPhone(String phone);
  
  /**
   * @return the contact number for this service
   */
  public String getPhone();
  
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
