package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Date;

import com.eaglegenomics.simlims.core.User;

/**
 * A single entry in the database-generated change log for an object.
 */
public interface ChangeLog {

  /**
   * The id of the entity associated with the ChangeLog.
   * 
   * @return The entity id. (e.g. the id of the Box, Pool, etc.)
   */
  public Long getId();

  /**
   * Set the id of the entity associated with the ChangeLog.
   * 
   * @param id The entity id. (e.g. the id of the Box, Pool, etc.)
   */
  public void setId(Long id);

  public String getColumnsChanged();

  public String getSummary();

  public Date getTime();

  public void setColumnsChanged(String columnsChanged);

  public void setSummary(String summary);

  public void setTime(Date time);

  public User getUser();

  public void setUser(User user);

}
