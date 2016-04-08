package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Date;

import com.eaglegenomics.simlims.core.User;

public interface SequencingParameters {
  public Long getId();

  public void setId(Long id);

  public String getName();

  public void setName(String string);

  /**
   * Gets the platform ID. This exists due to platforms not being in Hibernate.
   */
  public Long getPlatformId();

  public Platform getPlatform();

  public void setPlatform(Platform platform);

  public User getCreatedBy();

  public void setCreatedBy(User createdby);

  public Date getCreationDate();

  public void setCreationDate(Date creation);

  public User getUpdatedBy();

  public void setUpdatedBy(User updatedBy);

  public Date getLastUpdated();

  public void setLastUpdated(Date lastUpdated);
}
