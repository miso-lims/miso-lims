package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Date;

/**
 * A single entry in the database-generated change log for an object.
 */
public class ChangeLog {
  public String columnsChanged;
  public String summary;
  public Date time;

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

}
