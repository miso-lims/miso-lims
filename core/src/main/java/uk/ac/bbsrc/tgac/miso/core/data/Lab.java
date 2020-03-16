package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.util.Date;

import com.eaglegenomics.simlims.core.User;

/**
 * A lab within an Institute
 */
public interface Lab extends Serializable, Aliasable, Deletable {

  public void setAlias(String alias);

  public boolean isArchived();

  public void setArchived(boolean archived);
  
  /**
   * @return the Institute to which this Lab belongs
   */
  public Institute getInstitute();
  
  /**
   * Sets the Institute to which this Lab belongs
   * 
   * @param institute
   */
  public void setInstitute(Institute institute);

  public User getCreatedBy();

  public void setCreatedBy(User createdBy);

  public Date getCreationDate();

  public void setCreationDate(Date creationDate);

  public User getUpdatedBy();

  public void setUpdatedBy(User updatedBy);

  public Date getLastUpdated();

  public void setLastUpdated(Date lastUpdated);

  /**
   * Get custom label for dropdown options
   */
  public String getItemLabel();

}
