package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.util.Date;

import com.eaglegenomics.simlims.core.User;

public interface TissueType extends Serializable {

  public Long getId();

  public void setId(Long tissueTypeId);

  public String getAlias();

  public void setAlias(String alias);

  public String getDescription();

  public void setDescription(String description);

  public User getCreatedBy();

  public void setCreatedBy(User createdBy);

  public Date getCreationDate();

  public void setCreationDate(Date creationDate);

  public User getUpdatedBy();

  public void setUpdatedBy(User updatedBy);

  public Date getLastUpdated();

  public void setLastUpdated(Date lastUpdated);

  public String getItemLabel();

}
