package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.util.Date;

import com.eaglegenomics.simlims.core.User;

/**
 * A lab within an Institute
 */
public interface Lab extends Serializable {

  Long getId();

  void setId(Long id);

  String getAlias();

  void setAlias(String alias);
  
  /**
   * @return the Institute to which this Lab belongs
   */
  Institute getInstitute();
  
  /**
   * Sets the Institute to which this Lab belongs
   * 
   * @param institute
   */
  void setInstitute(Institute institute);

  User getCreatedBy();

  void setCreatedBy(User createdBy);

  Date getCreationDate();

  void setCreationDate(Date creationDate);

  User getUpdatedBy();

  void setUpdatedBy(User updatedBy);

  Date getLastUpdated();

  void setLastUpdated(Date lastUpdated);

  /**
   * Get custom label for dropdown options
   */
  String getItemLabel();

}
