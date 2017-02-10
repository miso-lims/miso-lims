package uk.ac.bbsrc.tgac.miso.core.data;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.eaglegenomics.simlims.core.User;

import uk.ac.bbsrc.tgac.miso.core.data.impl.UserImpl;

@MappedSuperclass
public abstract class AbstractChangeLog implements ChangeLog {

  @Column(length = 500)
  private String columnsChanged;

  @ManyToOne(targetEntity = UserImpl.class)
  @JoinColumn(name = "userId", nullable = false)
  private User user;

  @Column(name = "message", nullable = false)
  @Lob
  private String summary;

  @Column(name = "changeTime", nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date time;

  @Override
  public String getColumnsChanged() {
    return columnsChanged;
  }

  @Override
  public String getSummary() {
    return summary;
  }

  @Override
  public Date getTime() {
    return time;
  }

  @Override
  public void setColumnsChanged(String columnsChanged) {
    this.columnsChanged = columnsChanged;
  }

  @Override
  public void setSummary(String summary) {
    this.summary = summary;
  }

  @Override
  public void setTime(Date time) {
    this.time = time;
  }

  @Override
  public User getUser() {
    return user;
  }

  @Override
  public void setUser(User user) {
    this.user = user;
  }

}
