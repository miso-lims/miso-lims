package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Date;

import com.eaglegenomics.simlims.core.User;

/**
 * A single entry in the database-generated change log for an object.
 */
public class ChangeLog {
  private String columnsChanged;
  private String summary;
  private Date time;
  private User user;

  public String getColumnsChanged() {
    return columnsChanged;
  }

  public String getSummary() {
    return summary;
  }

  public Date getTime() {
    return time;
  }

  public void setColumnsChanged(String columnsChanged) {
    this.columnsChanged = columnsChanged;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public void setTime(Date time) {
    this.time = time;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

}
