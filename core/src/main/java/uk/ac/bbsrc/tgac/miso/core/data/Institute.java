package uk.ac.bbsrc.tgac.miso.core.data;

import java.io.Serializable;
import java.util.Date;

import com.eaglegenomics.simlims.core.User;

public interface Institute extends Serializable, Aliasable, Deletable {

  public void setAlias(String alias);

  public boolean isArchived();

  public void setArchived(boolean archived);

  public User getCreatedBy();

  public void setCreatedBy(User createdBy);

  public Date getCreationDate();

  public void setCreationDate(Date creationDate);

  public User getUpdatedBy();

  public void setUpdatedBy(User updatedBy);

  public Date getLastUpdated();

  public void setLastUpdated(Date lastUpdated);

}
