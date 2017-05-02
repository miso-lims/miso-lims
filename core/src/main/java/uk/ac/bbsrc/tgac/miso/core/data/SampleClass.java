package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.util.Date;

import com.eaglegenomics.simlims.core.User;

public interface SampleClass extends Serializable {

  Long getId();

  void setId(Long sampleClassId);

  String getAlias();

  void setAlias(String alias);

  User getCreatedBy();

  void setCreatedBy(User createdBy);

  Date getCreationDate();

  void setCreationDate(Date creationDate);

  User getUpdatedBy();

  void setUpdatedBy(User updatedBy);

  Date getLastUpdated();

  void setLastUpdated(Date lastUpdated);

  String getSampleCategory();

  void setSampleCategory(String sampleCategory);

  /**
   * @return the class identifier used in Sample alias generation
   */
  String getSuffix();

  /**
   * Sets the class identifier used in Sample alias generation
   * 
   * @param suffix
   */
  void setSuffix(String suffix);

  Boolean getDNAseTreatable();

  void setDNAseTreatable(Boolean treatable);
}